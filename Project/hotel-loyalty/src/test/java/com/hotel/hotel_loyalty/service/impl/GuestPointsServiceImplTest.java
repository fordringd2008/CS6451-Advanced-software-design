package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.OrderDO;
import com.hotel.hotel_loyalty.domain.PaymentMethodDO;
import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import com.hotel.hotel_loyalty.domain.ProductDO;
import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.mapper.OrderMapper;
import com.hotel.hotel_loyalty.mapper.PaymentMethodMapper;
import com.hotel.hotel_loyalty.mapper.PointsTransactionMapper;
import com.hotel.hotel_loyalty.mapper.ProductMapper;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import com.hotel.hotel_loyalty.payment.PaymentStrategyFactory;
import com.hotel.hotel_loyalty.vo.ConfirmPaymentVO;
import com.hotel.hotel_loyalty.vo.MyPointsVO;
import com.hotel.hotel_loyalty.vo.PointsMallVO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link GuestPointsServiceImpl} — query and fast-fail
 * validation paths. Full redeem chain is covered separately by the
 * integration test. No Spring context: all six dependencies are mocks.
 *
 * @author Fordring
 * @since 1.0
 */
class GuestPointsServiceImplTest {

    private final UserMapper userMapper = mock(UserMapper.class);
    private final ProductMapper productMapper = mock(ProductMapper.class);
    private final PaymentMethodMapper paymentMethodMapper = mock(PaymentMethodMapper.class);
    private final OrderMapper orderMapper = mock(OrderMapper.class);
    private final PointsTransactionMapper pointsTransactionMapper = mock(PointsTransactionMapper.class);
    private final PaymentStrategyFactory paymentStrategyFactory = mock(PaymentStrategyFactory.class);
    private final GuestPointsServiceImpl service = new GuestPointsServiceImpl(
            userMapper, productMapper, paymentMethodMapper, orderMapper,
            pointsTransactionMapper, paymentStrategyFactory);

    private final LocalDateTime now = LocalDateTime.of(2026, 10, 25, 0, 0, 0);

    private UserDO user(String uid, int points) {
        UserDO u = new UserDO();
        u.setUid(uid);
        u.setName("test " + uid);
        u.setCurrentPoint(points);
        u.setLevel(2);
        u.setRegisterTime(now);
        return u;
    }

    private ProductDO product(String pid, int remainingStock) {
        ProductDO p = new ProductDO();
        p.setPid(pid);
        p.setName("product " + pid);
        p.setImageUrl("http://test/" + pid + ".png");
        p.setTotalPointsRequired(800);
        p.setMinPointsRequired(400);
        p.setRemainingStock(remainingStock);
        return p;
    }

    @Test
    void testGetPointsMall_whenUserExists_shouldReturnBalanceAndProducts() {
        // given
        when(userMapper.selectByUid("guest")).thenReturn(user("guest", 5000));
        when(productMapper.selectAvailable()).thenReturn(List.of(product("P-003", 200)));
        // when
        PointsMallVO vo = service.getPointsMall("guest");
        // then
        assertThat(vo.getCurrentPoint()).isEqualTo(5000);
        assertThat(vo.getProducts()).hasSize(1);
        assertThat(vo.getProducts().get(0).getPid()).isEqualTo("P-003");
    }

    @Test
    void testGetPointsMall_whenUserNotFound_shouldThrow() {
        // given
        when(userMapper.selectByUid("ghost")).thenReturn(null);
        // when + then
        assertThatThrownBy(() -> service.getPointsMall("ghost"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("user not found");
    }

    @Test
    void testGetMyPoints_whenUserExists_shouldReturnPointsRedemptionsAndHistory() {
        // given — a guest with one paid redemption and two transactions (earn + redeem)
        when(userMapper.selectByUid("guest")).thenReturn(user("guest", 4200));
        OrderDO order = new OrderDO();
        order.setOid("O-TEST1");
        order.setPid("P-003");
        order.setPointsUsed(800);
        order.setStatus(2);
        order.setGmtCreate(now);
        when(orderMapper.selectByUid("guest")).thenReturn(List.of(order));
        when(productMapper.selectByPid("P-003")).thenReturn(product("P-003", 199));
        PointsTransactionDO earn = new PointsTransactionDO();
        earn.setPtid("PT-EARN01");
        earn.setType("EARN");
        earn.setPointsChange(200);
        earn.setRelatedOrderId(null);
        earn.setGmtCreate(now);
        PointsTransactionDO redeem = new PointsTransactionDO();
        redeem.setPtid("PT-REDEEM01");
        redeem.setType("REDEEM");
        redeem.setPointsChange(-800);
        redeem.setRelatedOrderId("O-TEST1");
        redeem.setGmtCreate(now);
        when(pointsTransactionMapper.selectByUid("guest")).thenReturn(List.of(redeem, earn));
        // when
        MyPointsVO vo = service.getMyPoints("guest");
        // then — header + redemption mapping (incl. status label)
        assertThat(vo.getName()).isEqualTo("test guest");
        assertThat(vo.getLevelLabel()).isEqualTo("L2");
        assertThat(vo.getCurrentPoint()).isEqualTo(4200);
        assertThat(vo.getRedemptions()).hasSize(1);
        assertThat(vo.getRedemptions().get(0).getProductName()).isEqualTo("product P-003");
        assertThat(vo.getRedemptions().get(0).getStatusLabel()).isEqualTo("Paid");
        // then — history mapping preserves order, type, signed delta and related order
        assertThat(vo.getHistory()).hasSize(2);
        assertThat(vo.getHistory().get(0).getPtid()).isEqualTo("PT-REDEEM01");
        assertThat(vo.getHistory().get(0).getType()).isEqualTo("REDEEM");
        assertThat(vo.getHistory().get(0).getPointsChange()).isEqualTo(-800);
        assertThat(vo.getHistory().get(0).getRelatedOrderId()).isEqualTo("O-TEST1");
        assertThat(vo.getHistory().get(1).getPtid()).isEqualTo("PT-EARN01");
        assertThat(vo.getHistory().get(1).getType()).isEqualTo("EARN");
        assertThat(vo.getHistory().get(1).getPointsChange()).isEqualTo(200);
        assertThat(vo.getHistory().get(1).getRelatedOrderId()).isNull();
    }

    @Test
    void testGetConfirmPayment_whenProductNotFound_shouldThrow() {
        // given
        when(userMapper.selectByUid("guest")).thenReturn(user("guest", 5000));
        when(productMapper.selectByPid("P-999")).thenReturn(null);
        // when + then
        assertThatThrownBy(() -> service.getConfirmPayment("guest", "P-999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("product not found");
    }

    @Test
    void testRedeem_whenProductOutOfStock_shouldThrow() {
        // given: product has zero remaining stock
        when(userMapper.selectByUid("guest")).thenReturn(user("guest", 5000));
        when(productMapper.selectByPid("P-003")).thenReturn(product("P-003", 0));
        when(paymentMethodMapper.selectByPmid("PM-1")).thenReturn(new PaymentMethodDO());
        RedemptionRequest request = new RedemptionRequest();
        request.setUid("guest");
        request.setPid("P-003");
        request.setPmid("PM-1");
        request.setPointsUsed(800);
        // when + then
        assertThatThrownBy(() -> service.redeem(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("out of stock");
        // and: no order written
        verify(orderMapper, never()).insert(any());
    }

    @Test
    void testRedeem_whenPaymentMethodNotFound_shouldThrow() {
        // given: PM-999 does not exist
        when(userMapper.selectByUid("guest")).thenReturn(user("guest", 5000));
        when(productMapper.selectByPid("P-003")).thenReturn(product("P-003", 5));
        when(paymentMethodMapper.selectByPmid("PM-999")).thenReturn(null);
        RedemptionRequest request = new RedemptionRequest();
        request.setUid("guest");
        request.setPid("P-003");
        request.setPmid("PM-999");
        request.setPointsUsed(800);
        // when + then
        assertThatThrownBy(() -> service.redeem(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("payment method not found");
        verify(orderMapper, never()).insert(any());
    }
}
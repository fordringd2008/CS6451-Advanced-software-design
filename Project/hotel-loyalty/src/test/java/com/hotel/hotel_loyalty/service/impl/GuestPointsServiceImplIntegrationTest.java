package com.hotel.hotel_loyalty.service.impl;

import com.hotel.hotel_loyalty.domain.OrderDO;
import com.hotel.hotel_loyalty.domain.PointsTransactionDO;
import com.hotel.hotel_loyalty.dto.RedemptionRequest;
import com.hotel.hotel_loyalty.mapper.OrderMapper;
import com.hotel.hotel_loyalty.mapper.PointsTransactionMapper;
import com.hotel.hotel_loyalty.mapper.ProductMapper;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for the full redeem chain (UC3) — real H2, real SQL,
 * real strategy beans. @Transactional rolls back every test so the seed
 * data (guest 5000 pts, P-003 costs 800/min 400/stock 200, PM-1..3)
 * stays pristine for the next test.
 *
 * @author Fordring
 * @since 1.0
 */
@SpringBootTest
@Transactional
class GuestPointsServiceImplIntegrationTest {

    @Autowired
    private GuestPointsService guestPointsService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PointsTransactionMapper pointsTransactionMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RedemptionRequest redeemRequest(String uid, String pid, String pmid, int points) {
        RedemptionRequest r = new RedemptionRequest();
        r.setUid(uid);
        r.setPid(pid);
        r.setPmid(pmid);
        r.setPointsUsed(points);
        return r;
    }

    @Test
    void testRedeem_whenPurePoints_shouldCompleteFullChain() {
        // given: seed guest has 5000 pts; P-003 costs 800 (min 400), stock 200
        // when
        String oid = guestPointsService.redeem(redeemRequest("guest", "P-003", "PM-1", 800));
        // then 1: points deducted 5000 -> 4200 (CAS)
        assertThat(userMapper.selectByUid("guest").getCurrentPoint()).isEqualTo(4200);
        // then 2: stock deducted 200 -> 199 (CAS)
        assertThat(productMapper.selectByPid("P-003").getRemainingStock()).isEqualTo(199);
        // then 3: order created and PAID (status 2)
        OrderDO order = orderMapper.selectByOid(oid);
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo(2);
        assertThat(order.getPointsUsed()).isEqualTo(800);
        assertThat(order.getPmid()).isEqualTo("PM-1");
        // then 4: audit trail — REDEEM transaction of -800 linked to the order
        List<PointsTransactionDO> txs = pointsTransactionMapper.selectByRelatedOrderId(oid);
        assertThat(txs).hasSize(1);
        assertThat(txs.get(0).getPointsChange()).isEqualTo(-800);
        assertThat(txs.get(0).getType()).isEqualTo("REDEEM");
    }

    @Test
    void testRedeem_whenInsufficientBalance_shouldThrowAndNotDeduct() {
        // given: P-001 costs 10000, guest only has 5000
        int balanceBefore = userMapper.selectByUid("guest").getCurrentPoint();
        // when + then
        assertThatThrownBy(() -> guestPointsService.redeem(
                redeemRequest("guest", "P-001", "PM-1", 10000)))
                .isInstanceOf(IllegalArgumentException.class);
        // and: balance unchanged (validation happens before any write)
        assertThat(userMapper.selectByUid("guest").getCurrentPoint()).isEqualTo(balanceBefore);
    }

    @Test
    void testRedeem_whenProductSoldOut_shouldThrow() {
        // given: force P-003 stock to zero
        jdbcTemplate.update("UPDATE product SET remaining_stock = 0 WHERE pid = 'P-003'");
        // when + then
        assertThatThrownBy(() -> guestPointsService.redeem(
                redeemRequest("guest", "P-003", "PM-1", 800)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("out of stock");
    }

    @Test
    void testRedeem_whenPaymentMethodNotExists_shouldThrow() {
        // when + then
        assertThatThrownBy(() -> guestPointsService.redeem(
                redeemRequest("guest", "P-003", "PM-999", 800)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("payment method not found");
    }

    @Test
    void testRedeem_whenUserNotExists_shouldThrow() {
        // when + then
        assertThatThrownBy(() -> guestPointsService.redeem(
                redeemRequest("ghost", "P-003", "PM-1", 800)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("user not found");
    }

    @Test
    void testRedeem_whenProductNotExists_shouldThrow() {
        // when + then
        assertThatThrownBy(() -> guestPointsService.redeem(
                redeemRequest("guest", "P-999", "PM-1", 800)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("product not found");
    }
}
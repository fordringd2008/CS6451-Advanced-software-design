package com.hotel.hotel_loyalty.mapper;

import com.hotel.hotel_loyalty.domain.OrderDO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Slice tests for {@link OrderMapper} — real H2, real SQL.
 * Focus: CAS status transition + countActiveByPid (UC6 delete precondition).
 *
 * @author Fordring
 * @since 1.0
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    private LocalDateTime now = LocalDateTime.of(2026, 10, 25, 0, 0, 0);

    /** Insert an order with the given status (1 pending / 2 paid / 3 completed / 5 cancelled). */
    private void insertOrder(String oid, String pid, int status) {
        OrderDO order = new OrderDO();
        order.setOid(oid);
        order.setUid("test_u_order");
        order.setPid(pid);
        order.setPmid("PM-1");
        order.setPointsUsed(100);
        order.setCashUsed(null);
        order.setVoucherUsed(null);
        order.setStatus(status);
        order.setGmtCreate(now);
        order.setGmtModified(now);
        orderMapper.insert(order);
    }

    @Test
    void testInsertAndSelectByOid_shouldRoundTrip() {
        // given
        insertOrder("test_o_001", "test_p_order", 1);
        // when
        OrderDO found = orderMapper.selectByOid("test_o_001");
        // then
        assertThat(found).isNotNull();
        assertThat(found.getPid()).isEqualTo("test_p_order");
        assertThat(found.getStatus()).isEqualTo(1);
        assertThat(found.getPointsUsed()).isEqualTo(100);
    }

    @Test
    void testCompareAndUpdateStatus_whenCurrentStatusMatches_shouldReturnOneAndUpdate() {
        // given: order is PENDING_PAYMENT (1); CAS expects 1 -> PAID (2)
        insertOrder("test_o_010", "test_p_order", 1);
        // when
        int affected = orderMapper.compareAndUpdateStatus("test_o_010", 1, 2, now);
        // then
        assertThat(affected).isEqualTo(1);
        assertThat(orderMapper.selectByOid("test_o_010").getStatus()).isEqualTo(2);
    }

    @Test
    void testCompareAndUpdateStatus_whenCurrentStatusMismatch_shouldReturnZeroAndNotUpdate() {
        // given: order is PAID (2); CAS expects 1 (already moved by another thread)
        insertOrder("test_o_020", "test_p_order", 2);
        // when
        int affected = orderMapper.compareAndUpdateStatus("test_o_020", 1, 2, now);
        // then: CAS fails, status unchanged
        assertThat(affected).isZero();
        assertThat(orderMapper.selectByOid("test_o_020").getStatus()).isEqualTo(2);
    }

    @Test
    void testCountActiveByPid_whenActiveOrdersExist_shouldReturnPositive() {
        // given: one PENDING_PAYMENT (1) + one PAID (2) for the same product
        insertOrder("test_o_030", "test_p_active", 1);
        insertOrder("test_o_031", "test_p_active", 2);
        // when: UC6 delete precondition check
        int count = orderMapper.countActiveByPid("test_p_active");
        // then
        assertThat(count).isPositive().isEqualTo(2);
    }

    @Test
    void testCountActiveByPid_whenOnlyFinalisedOrders_shouldReturnZero() {
        // given: one COMPLETED (3) + one CANCELLED (5) — both finalised
        insertOrder("test_o_040", "test_p_final", 3);
        insertOrder("test_o_041", "test_p_final", 5);
        // when
        int count = orderMapper.countActiveByPid("test_p_final");
        // then: no active orders — UC6 delete allowed
        assertThat(count).isZero();
    }
}
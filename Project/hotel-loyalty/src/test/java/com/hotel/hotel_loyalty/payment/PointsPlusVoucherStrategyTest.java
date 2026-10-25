package com.hotel.hotel_loyalty.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PointsPlusVoucherStrategy} (Type 3 — points + voucher).
 * Pure logic: no Spring context, no mocks.
 *
 * @author Fordring
 * @since 1.0
 */
class PointsPlusVoucherStrategyTest {

    private final PointsPlusVoucherStrategy strategy = new PointsPlusVoucherStrategy();

    /** Build a points+voucher request: no cash. */
    private PaymentRequest voucherRequest(int pointsUsed, BigDecimal voucher,
                                          int minPoints, int totalPoints, int balance) {
        return new PaymentRequest("u1", "p1", "PM-3",
                pointsUsed, null, voucher, totalPoints, minPoints, balance);
    }

    @Test
    void testValidate_whenPointsAboveMinAndVoucherPositive_shouldSucceed() {
        // given
        PaymentRequest req = voucherRequest(60, BigDecimal.TEN, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testValidate_whenPointsExactlyAtMin_shouldSucceed() {
        // given: boundary — points exactly equal the minimum
        PaymentRequest req = voucherRequest(50, BigDecimal.TEN, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testValidate_whenPointsBelowMin_shouldFail() {
        // given
        PaymentRequest req = voucherRequest(40, BigDecimal.TEN, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("at least the minimum");
    }

    @Test
    void testValidate_whenVoucherZero_shouldFail() {
        // given: voucher must be positive
        PaymentRequest req = voucherRequest(60, BigDecimal.ZERO, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("positive voucher");
    }

    @Test
    void testValidate_whenCashPresent_shouldFail() {
        // given: points+voucher must not carry cash
        PaymentRequest req = new PaymentRequest("u1", "p1", "PM-3",
                60, BigDecimal.TEN, BigDecimal.TEN, 100, 50, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("must not include cash");
    }

    @Test
    void testValidate_whenInsufficientBalance_shouldFail() {
        // given
        PaymentRequest req = voucherRequest(60, BigDecimal.TEN, 50, 100, 50);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("insufficient points");
    }
}
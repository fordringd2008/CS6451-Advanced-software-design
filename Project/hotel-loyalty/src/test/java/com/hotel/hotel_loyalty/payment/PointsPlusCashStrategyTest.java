package com.hotel.hotel_loyalty.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PointsPlusCashStrategy} (Type 2 — points + cash).
 * Pure logic: no Spring context, no mocks.
 *
 * @author Fordring
 * @since 1.0
 */
class PointsPlusCashStrategyTest {

    private final PointsPlusCashStrategy strategy = new PointsPlusCashStrategy();

    /** Build a points+cash request: no voucher. */
    private PaymentRequest cashRequest(int pointsUsed, BigDecimal cash,
                                       int minPoints, int totalPoints, int balance) {
        return new PaymentRequest("u1", "p1", "PM-2",
                pointsUsed, cash, null, totalPoints, minPoints, balance);
    }

    @Test
    void testValidate_whenPointsAboveMinAndCashPositive_shouldSucceed() {
        // given: points between min and full price, positive cash, balance enough
        PaymentRequest req = cashRequest(60, new BigDecimal("4.00"), 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testValidate_whenPointsExactlyAtMin_shouldSucceed() {
        // given: boundary — points exactly equal the minimum
        PaymentRequest req = cashRequest(50, new BigDecimal("5.00"), 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testValidate_whenPointsBelowMin_shouldFail() {
        // given
        PaymentRequest req = cashRequest(40, BigDecimal.TEN, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("at least the minimum");
    }

    @Test
    void testValidate_whenCashZero_shouldFail() {
        // given: cash must be positive
        PaymentRequest req = cashRequest(60, BigDecimal.ZERO, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("positive cash");
    }

    @Test
    void testValidate_whenPointsCoverFullPrice_shouldFail() {
        // given: points already cover full price — should use points-only instead
        PaymentRequest req = cashRequest(100, BigDecimal.TEN, 50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("points-only");
    }

    @Test
    void testValidate_whenInsufficientBalance_shouldFail() {
        // given
        PaymentRequest req = cashRequest(60, BigDecimal.TEN, 50, 100, 50);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("insufficient points");
    }
}
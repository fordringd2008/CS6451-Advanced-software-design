package com.hotel.hotel_loyalty.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PointsOnlyStrategy} (Type 1 — pure points).
 * Pure logic: no Spring context, no mocks (PaymentRequest carries the balance).
 *
 * @author Fordring
 * @since 1.0
 */
class PointsOnlyStrategyTest {

    private final PointsOnlyStrategy strategy = new PointsOnlyStrategy();

    /** Build a points-only request: no cash, no voucher. */
    private PaymentRequest pointsOnlyRequest(int pointsUsed, int totalPoints, int balance) {
        return new PaymentRequest("u1", "p1", "PM-1",
                pointsUsed, null, null, totalPoints, 0, balance);
    }

    @Test
    void testValidate_whenPointsEqualPriceAndBalanceSufficient_shouldSucceed() {
        // given
        PaymentRequest req = pointsOnlyRequest(100, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testValidate_whenBalanceExactlyEqualsPrice_shouldSucceed() {
        // given: boundary — balance exactly equals the points needed
        PaymentRequest req = pointsOnlyRequest(100, 100, 100);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testValidate_whenPointsLessThanFullPrice_shouldFail() {
        // given
        PaymentRequest req = pointsOnlyRequest(50, 100, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("points must equal");
    }

    @Test
    void testValidate_whenCashPresent_shouldFail() {
        // given: points-only must not carry a cash supplement
        PaymentRequest req = new PaymentRequest("u1", "p1", "PM-1",
                100, BigDecimal.TEN, null, 100, 0, 200);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("must not include cash or voucher");
    }

    @Test
    void testValidate_whenInsufficientBalance_shouldFail() {
        // given
        PaymentRequest req = pointsOnlyRequest(100, 100, 50);
        // when
        PaymentResult result = strategy.validate(req);
        // then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("insufficient points");
    }
}
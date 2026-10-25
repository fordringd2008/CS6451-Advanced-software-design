package com.hotel.hotel_loyalty.payment;

import com.hotel.hotel_loyalty.enums.PaymentTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link PaymentStrategyFactory}.
 * Pure logic: strategies are real instances (stateless), no Spring context.
 *
 * @author Fordring
 * @since 1.0
 */
class PaymentStrategyFactoryTest {

    private final PaymentStrategyFactory factory = new PaymentStrategyFactory(List.of(
            new PointsOnlyStrategy(),
            new PointsPlusCashStrategy(),
            new PointsPlusVoucherStrategy()));

    @Test
    void testGetStrategy_whenTypePointsOnly_shouldReturnPointsOnlyStrategy() {
        // given: factory with all three strategies registered
        // when
        PaymentStrategy strategy = factory.getStrategy(PaymentTypeEnum.POINTS_ONLY);
        // then
        assertThat(strategy).isInstanceOf(PointsOnlyStrategy.class);
    }

    @Test
    void testGetStrategy_whenTypePointsPlusCash_shouldReturnCashStrategy() {
        // when
        PaymentStrategy strategy = factory.getStrategy(PaymentTypeEnum.POINTS_PLUS_CASH);
        // then
        assertThat(strategy).isInstanceOf(PointsPlusCashStrategy.class);
    }

    @Test
    void testGetStrategy_whenTypePointsPlusVoucher_shouldReturnVoucherStrategy() {
        // when
        PaymentStrategy strategy = factory.getStrategy(PaymentTypeEnum.POINTS_PLUS_VOUCHER);
        // then
        assertThat(strategy).isInstanceOf(PointsPlusVoucherStrategy.class);
    }

    @Test
    void testGetStrategy_whenTypeNotRegistered_shouldThrow() {
        // given: a factory with no strategies registered
        PaymentStrategyFactory emptyFactory = new PaymentStrategyFactory(List.of());
        // when + then
        assertThatThrownBy(() -> emptyFactory.getStrategy(PaymentTypeEnum.POINTS_ONLY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no payment strategy registered");
    }
}
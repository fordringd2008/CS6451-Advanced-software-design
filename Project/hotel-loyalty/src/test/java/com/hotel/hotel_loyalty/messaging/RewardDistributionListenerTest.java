package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link RewardDistributionListener}.
 * <p>
 * The listener deducts one unit of product stock as the reward-
 * distribution step of the in-process PointsDeductedEvent
 * (architecture document 5.0 — Event-Driven Architecture,
 * Open/Closed Principle: future listeners can subscribe to the
 * same event without touching this class).
 *
 * @author Fordring
 * @since 1.0
 */
class RewardDistributionListenerTest {

    @Test
    @DisplayName("event received: listener deducts one unit of stock")
    void testOnPointsDeducted_shouldDeductStock() {
        // given: a points-deducted event and stock available
        ProductMapper productMapper = mock(ProductMapper.class);
        when(productMapper.deductStock(eq("P-003"), eq(1), any(LocalDateTime.class)))
                .thenReturn(1);
        RewardDistributionListener listener = new RewardDistributionListener(productMapper);
        PointsDeductedEvent event = new PointsDeductedEvent("O-1", "guest", "P-003", 800);
        // when + then: listener deducts stock without error
        assertThatCode(() -> listener.onPointsDeducted(event))
                .doesNotThrowAnyException();
        verify(productMapper).deductStock(eq("P-003"), eq(1), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("stock exhausted concurrently: listener fails the distribution")
    void testOnPointsDeducted_whenStockSoldOut_shouldThrow() {
        // given: the conditional UPDATE affects no row (sold out concurrently)
        ProductMapper productMapper = mock(ProductMapper.class);
        when(productMapper.deductStock(anyString(), anyInt(), any(LocalDateTime.class)))
                .thenReturn(0);
        RewardDistributionListener listener = new RewardDistributionListener(productMapper);
        PointsDeductedEvent event = new PointsDeductedEvent("O-1", "guest", "P-003", 800);
        // when + then: listener signals the failure so the transaction rolls back
        assertThatThrownBy(() -> listener.onPointsDeducted(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no longer in stock");
    }
}
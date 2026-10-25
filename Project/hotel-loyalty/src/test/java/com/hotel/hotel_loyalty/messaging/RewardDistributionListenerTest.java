package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link RewardDistributionListener}.
 * <p>
 * The listener decouples reward distribution (stock deduction) from the
 * redemption flow via an in-process event (architecture document 5.0 —
 * Event-Driven Architecture). These tests verify both outcomes of the
 * stock-deduction step: success and concurrent exhaustion.
 *
 * @author Fordring
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class RewardDistributionListenerTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private RewardDistributionListener listener;

    @Test
    @DisplayName("stock available: deductStock returns 1, reward distributed without error")
    void testOnPointsDeducted_whenStockAvailable_shouldDeductOne() {
        // given: product P-003 has stock; CAS deduct will succeed
        when(productMapper.deductStock(eq("P-003"), eq(1), any())).thenReturn(1);
        PointsDeductedEvent event = new PointsDeductedEvent("O-1", "guest", "P-003", 800);
        // when: listener handles the event
        listener.onPointsDeducted(event);
        // then: stock deduction invoked with quantity 1, no exception
        verify(productMapper).deductStock(eq("P-003"), eq(1), any());
    }

    @Test
    @DisplayName("stock exhausted: deductStock returns 0, exception propagates for compensation")
    void testOnPointsDeducted_whenStockExhausted_shouldThrowIllegalState() {
        // given: product P-001 is sold out; CAS deduct returns 0
        when(productMapper.deductStock(eq("P-001"), eq(1), any())).thenReturn(0);
        PointsDeductedEvent event = new PointsDeductedEvent("O-2", "guest", "P-001", 10000);
        // when + then: listener throws so the caller can compensate
        assertThatThrownBy(() -> listener.onPointsDeducted(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("insufficient stock")
                .hasMessageContaining("P-001");
    }
}
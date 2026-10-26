package com.hotel.hotel_loyalty.messaging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Pure unit tests for {@link RewardDistributionListener}.
 * <p>
 * The listener is notification-only (logging) — stock deduction stays
 * inside the redemption transaction for explicit concurrency control
 * (architecture document 5.0 — Event-Driven Architecture, Open/Closed
 * Principle: future listeners can subscribe to the same event).
 *
 * @author Fordring
 * @since 1.0
 */
class RewardDistributionListenerTest {

    @Test
    @DisplayName("event received: listener logs notification without error")
    void testOnPointsDeducted_shouldLogNotificationWithoutError() {
        // given: a points-deducted event for a completed redemption
        PointsDeductedEvent event = new PointsDeductedEvent("O-1", "guest", "P-003", 800);
        // when + then: listener handles the event purely by logging, no exception
        assertThatCode(() -> new RewardDistributionListener().onPointsDeducted(event))
                .doesNotThrowAnyException();
    }
}
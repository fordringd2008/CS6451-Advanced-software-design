package com.hotel.hotel_loyalty.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listens for {@link PointsDeductedEvent} and logs the reward-distribution
 * step (architecture document 5.0 — Event-Driven Architecture).
 * <p>
 * "A separate listener then gives out the reward. This keeps reward
 * distribution apart from point deduction. It also follows the
 * Open/Closed Principle: if we change how rewards are given later,
 * we only add or replace a listener."
 * <p>
 * Current behaviour: notification only (logging). Stock deduction stays
 * inside the redemption transaction for explicit concurrency control.
 * Future listeners (email, voucher issuance, …) can subscribe to the
 * same event without touching this class.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class RewardDistributionListener {

    private static final Logger log = LoggerFactory.getLogger(RewardDistributionListener.class);

    @EventListener
    public void onPointsDeducted(PointsDeductedEvent event) {
        log.info("reward notification: order={}, product={}, pointsUsed={}",
                event.getOid(), event.getPid(), event.getPointsUsed());
        log.info("reward distribution step acknowledged for order {}", event.getOid());
    }
}
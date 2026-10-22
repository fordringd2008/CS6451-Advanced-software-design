package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Listens for {@link PointsDeductedEvent} and distributes the reward
 * (architecture document 5.0 — Event-Driven Architecture).
 * <p>
 * "A separate listener then gives out the reward. This keeps reward
 * distribution apart from point deduction. It also follows the
 * Open/Closed Principle: if we change how rewards are given later,
 * we only add or replace a listener."
 * <p>
 * Current reward = stock deduction (UC3 updateRemainingStock step).
 * Future listeners (email, voucher issuance, …) can subscribe to the
 * same event without touching this class.
 *
 * @author Fordring
 * @since 1.0
 */
@Component
public class RewardDistributionListener {

    private static final Logger log = LoggerFactory.getLogger(RewardDistributionListener.class);

    private final ProductMapper productMapper;

    public RewardDistributionListener(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @EventListener
    public void onPointsDeducted(PointsDeductedEvent event) {
        log.info("distributing reward for order {}: product={}, pointsUsed={}",
                event.getOid(), event.getPid(), event.getPointsUsed());

        // One order = one product, so quantity is always 1.
        int updated = productMapper.deductStock(
                event.getPid(), 1, LocalDateTime.now());

        if (updated == 0) {
            // Concurrent stock exhaustion — propagate so the caller can compensate.
            throw new IllegalStateException(
                    "reward distribution failed: insufficient stock for product " + event.getPid());
        }

        log.info("reward distributed: order={}, product={}, stock deducted", event.getOid(), event.getPid());
    }
}
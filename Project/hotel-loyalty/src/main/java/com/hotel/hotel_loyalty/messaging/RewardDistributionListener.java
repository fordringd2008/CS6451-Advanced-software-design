package com.hotel.hotel_loyalty.messaging;

import com.hotel.hotel_loyalty.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Listens for {@link PointsDeductedEvent} and distributes the reward by
 * deducting one unit of product stock (architecture document 5.0 —
 * Event-Driven Architecture).
 * <p>
 * "After points are deducted, the system sends a 'PointsDeducted' event.
 * A separate listener then gives out the reward. This keeps reward
 * distribution apart from point deduction. It also follows the
 * Open/Closed Principle: if we change how rewards are given later,
 * we only add or replace a listener."
 * <p>
 * Spring's {@code @EventListener} executes synchronously inside the
 * publisher's transaction, so this deduction participates in the same
 * redemption transaction — atomicity is preserved while the reward
 * logic stays decoupled from the point-deduction service. The
 * conditional UPDATE (remaining_stock >= 1) is the concurrency guard:
 * when it affects no row the product sold out concurrently and the
 * thrown exception rolls the whole redemption back.
 * Future listeners (email, voucher issuance, …) can subscribe to the
 * same event without touching this class.
 *
 * @author wukewei
 * @since 1.0
 */
@Component
public class RewardDistributionListener {

    /** One product is redeemed per order. */
    private static final int SINGLE_ITEM_QUANTITY = 1;

    private static final Logger log = LoggerFactory.getLogger(RewardDistributionListener.class);

    private final ProductMapper productMapper;

    /**
     * Creates the reward distribution listener.
     *
     * @param productMapper product data access object used to deduct stock
     */
    public RewardDistributionListener(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @EventListener
    public void onPointsDeducted(PointsDeductedEvent event) {
        log.info("reward notification: order={}, product={}, pointsUsed={}",
                event.getOid(), event.getPid(), event.getPointsUsed());
        // Deduct stock as the reward-distribution step (payment has already
        // settled inside the redemption transaction). The conditional UPDATE
        // is the concurrency guard; zero affected rows means the product sold
        // out concurrently, so fail the transaction.
        if (productMapper.deductStock(event.getPid(), SINGLE_ITEM_QUANTITY, LocalDateTime.now())
                != SINGLE_ITEM_QUANTITY) {
            throw new IllegalStateException("product is no longer in stock: " + event.getPid());
        }
        log.info("reward distribution step acknowledged for order {}", event.getOid());
    }
}
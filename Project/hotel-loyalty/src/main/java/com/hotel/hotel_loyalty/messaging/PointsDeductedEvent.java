package com.hotel.hotel_loyalty.messaging;

import lombok.Getter;
import lombok.ToString;

/**
 * In-process event published after a guest's points are successfully
 * deducted in the redemption flow (architecture document 5.0 —
 * Event-Driven Architecture).
 * <p>
 * "After points are deducted, the system sends a 'PointsDeducted' event.
 * A separate listener then gives out the reward. This keeps reward
 * distribution apart from point deduction."
 * <p>
 * Published via Spring ApplicationEventPublisher (monolith, in-process);
 * the JMS queue is reserved for UC5 cross-failure persistence.
 *
 * @author Fordring
 * @since 1.0
 */
@Getter
@ToString
public class PointsDeductedEvent {

    /** Order that triggered the deduction. */
    private final String oid;

    /** User whose points were deducted. */
    private final String uid;

    /** Product to be given out as the reward. */
    private final String pid;

    /** Points that were deducted (for audit logging). */
    private final Integer pointsUsed;

    public PointsDeductedEvent(String oid, String uid, String pid, Integer pointsUsed) {
        this.oid = oid;
        this.uid = uid;
        this.pid = pid;
        this.pointsUsed = pointsUsed;
    }
}
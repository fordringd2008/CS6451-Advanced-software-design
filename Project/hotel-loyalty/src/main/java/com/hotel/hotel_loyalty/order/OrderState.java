package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;

/**
 * State interface for the order lifecycle (order state diagram).
 * <p>
 * Transitions:
 * <pre>
 *   PENDING_PAYMENT --pay-->     PAID
 *   PENDING_PAYMENT --cancel-->  CANCELLED
 *   PAID           --complete--> COMPLETED
 *   PAID           --cancel-->   CANCELLED  (refund logic deferred — conflict #5 unresolved)
 *   COMPLETED / CANCELLED — terminal
 * </pre>
 *
 * @author wukewei
 * @since 1.0
 */
public interface OrderState {

    OrderStatusEnum getStatus();

    /** Transition to PAID. Throws if not allowed from current state. */
    void pay(OrderContext context);

    /** Transition to COMPLETED. Throws if not allowed from current state. */
    void complete(OrderContext context);

    /** Transition to CANCELLED. Throws if not allowed from current state. */
    void cancel(OrderContext context);
}
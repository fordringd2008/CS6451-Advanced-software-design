package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;

/**
 * In-memory state machine holder for one order. The Service layer is
 * responsible for persisting state changes (via OrderMapper) after each
 * successful transition.
 *
 * @author wukewei
 * @since 1.0
 */
public class OrderContext {

    private final String oid;
    private OrderState currentState;

    public OrderContext(String oid, OrderState initialState) {
        this.oid = oid;
        this.currentState = initialState;
    }

    /**
     * Rebuild the context from a persisted status code.
     *
     * @param oid      order business key
     * @param statusDb status code from DB (1/2/3/5)
     * @return context positioned at the matching state
     * @throws IllegalArgumentException if the code is not a known order status
     */
    public static OrderContext fromDbStatus(String oid, Integer statusDb) {
        OrderStatusEnum status = OrderStatusEnum.fromCode(statusDb);
        // fromCode returns null for unknown codes (e.g. 4 — intentionally skipped);
        // switching on null would throw NPE before reaching the default branch.
        if (status == null) {
            throw new IllegalArgumentException("unknown order status code: " + statusDb);
        }
        switch (status) {
            case PENDING_PAYMENT:
                return new OrderContext(oid, PendingPaymentState.INSTANCE);
            case PAID:
                return new OrderContext(oid, PaidState.INSTANCE);
            case COMPLETED:
                return new OrderContext(oid, CompletedState.INSTANCE);
            case CANCELLED:
                return new OrderContext(oid, CancelledState.INSTANCE);
            default:
                throw new IllegalArgumentException("unknown order status code: " + statusDb);
        }
    }

    public String getOid() {
        return oid;
    }

    public OrderState getCurrentState() {
        return currentState;
    }

    /** Called by state objects to move the machine. */
    void setState(OrderState newState) {
        this.currentState = newState;
    }

    public void pay() {
        currentState.pay(this);
    }

    public void complete() {
        currentState.complete(this);
    }

    public void cancel() {
        currentState.cancel(this);
    }
}
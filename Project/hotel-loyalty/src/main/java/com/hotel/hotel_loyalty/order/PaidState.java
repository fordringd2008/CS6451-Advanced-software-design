package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;

/**
 * Order has been paid (status code 2).
 * Note: cancellation from PAID is allowed by the state diagram, but the
 * points-refund side-effect is deferred (consistency conflict #5 unresolved).
 *
 * @author Fordring
 * @since 1.0
 */
public class PaidState implements OrderState {

    public static final PaidState INSTANCE = new PaidState();

    private PaidState() {
    }

    @Override
    public OrderStatusEnum getStatus() {
        return OrderStatusEnum.PAID;
    }

    @Override
    public void pay(OrderContext context) {
        throw new IllegalStateException("order is already paid");
    }

    @Override
    public void complete(OrderContext context) {
        context.setState(CompletedState.INSTANCE);
    }

    @Override
    public void cancel(OrderContext context) {
        context.setState(CancelledState.INSTANCE);
    }
}
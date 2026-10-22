package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;

/**
 * Initial order state — awaiting payment (status code 1).
 *
 * @author Fordring
 * @since 1.0
 */
public class PendingPaymentState implements OrderState {

    public static final PendingPaymentState INSTANCE = new PendingPaymentState();

    private PendingPaymentState() {
    }

    @Override
    public OrderStatusEnum getStatus() {
        return OrderStatusEnum.PENDING_PAYMENT;
    }

    @Override
    public void pay(OrderContext context) {
        context.setState(PaidState.INSTANCE);
    }

    @Override
    public void complete(OrderContext context) {
        throw new IllegalStateException("cannot complete an unpaid order");
    }

    @Override
    public void cancel(OrderContext context) {
        context.setState(CancelledState.INSTANCE);
    }
}
package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;

/**
 * Terminal state — order fulfilled (status code 3). No further transitions.
 *
 * @author Fordring
 * @since 1.0
 */
public class CompletedState implements OrderState {

    public static final CompletedState INSTANCE = new CompletedState();

    private CompletedState() {
    }

    @Override
    public OrderStatusEnum getStatus() {
        return OrderStatusEnum.COMPLETED;
    }

    @Override
    public void pay(OrderContext context) {
        throw new IllegalStateException("cannot pay a completed order");
    }

    @Override
    public void complete(OrderContext context) {
        throw new IllegalStateException("order is already completed");
    }

    @Override
    public void cancel(OrderContext context) {
        throw new IllegalStateException("cannot cancel a completed order");
    }
}
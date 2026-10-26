package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;

/**
 * Terminal state — order cancelled (status code 5). No further transitions.
 *
 * @author wukewei
 * @since 1.0
 */
public class CancelledState implements OrderState {

    public static final CancelledState INSTANCE = new CancelledState();

    private CancelledState() {
    }

    @Override
    public OrderStatusEnum getStatus() {
        return OrderStatusEnum.CANCELLED;
    }

    @Override
    public void pay(OrderContext context) {
        throw new IllegalStateException("cannot pay a cancelled order");
    }

    @Override
    public void complete(OrderContext context) {
        throw new IllegalStateException("cannot complete a cancelled order");
    }

    @Override
    public void cancel(OrderContext context) {
        throw new IllegalStateException("order is already cancelled");
    }
}
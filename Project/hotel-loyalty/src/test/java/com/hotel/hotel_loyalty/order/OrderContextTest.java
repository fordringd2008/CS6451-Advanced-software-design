package com.hotel.hotel_loyalty.order;

import com.hotel.hotel_loyalty.enums.OrderStatusEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link OrderContext} and all four state classes
 * (PendingPaymentState / PaidState / CancelledState / CompletedState).
 * Pure logic: no mocks — state classes are self-contained singletons.
 * Aligned with order_state_diagram_1.0.svg: 4 states, status codes 1/2/3/5.
 *
 * @author Fordring
 * @since 1.0
 */
class OrderContextTest {

    // ---- fromDbStatus rebuilds (codes 1/2/3/5; code 4 intentionally skipped) ----

    @Test
    void testFromDbStatus_whenCode1_shouldRebuildPendingPayment() {
        // given + when
        OrderContext ctx = OrderContext.fromDbStatus("o1", 1);
        // then
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.PENDING_PAYMENT);
    }

    @Test
    void testFromDbStatus_whenCode2_shouldRebuildPaid() {
        OrderContext ctx = OrderContext.fromDbStatus("o1", 2);
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.PAID);
    }

    @Test
    void testFromDbStatus_whenCode3_shouldRebuildCompleted() {
        OrderContext ctx = OrderContext.fromDbStatus("o1", 3);
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.COMPLETED);
    }

    @Test
    void testFromDbStatus_whenCode5_shouldRebuildCancelled() {
        OrderContext ctx = OrderContext.fromDbStatus("o1", 5);
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.CANCELLED);
    }

    @Test
    void testFromDbStatus_whenCode4Skipped_shouldThrow() {
        // given: code 4 is intentionally skipped per candidate_objects
        assertThatThrownBy(() -> OrderContext.fromDbStatus("o1", 4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown order status code");
    }

    @Test
    void testFromDbStatus_whenNullCode_shouldThrow() {
        assertThatThrownBy(() -> OrderContext.fromDbStatus("o1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown order status code");
    }

    // ---- PENDING_PAYMENT transitions ----

    @Test
    void testPay_whenPendingPayment_shouldTransitionToPaid() {
        // given
        OrderContext ctx = new OrderContext("o1", PendingPaymentState.INSTANCE);
        // when
        ctx.pay();
        // then
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.PAID);
    }

    @Test
    void testCancel_whenPendingPayment_shouldTransitionToCancelled() {
        OrderContext ctx = new OrderContext("o1", PendingPaymentState.INSTANCE);
        ctx.cancel();
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.CANCELLED);
    }

    @Test
    void testComplete_whenPendingPayment_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", PendingPaymentState.INSTANCE);
        assertThatThrownBy(ctx::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot complete an unpaid order");
    }

    // ---- PAID transitions ----

    @Test
    void testComplete_whenPaid_shouldTransitionToCompleted() {
        OrderContext ctx = new OrderContext("o1", PaidState.INSTANCE);
        ctx.complete();
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.COMPLETED);
    }

    @Test
    void testPay_whenPaid_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", PaidState.INSTANCE);
        assertThatThrownBy(ctx::pay)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already paid");
    }

    @Test
    void testCancel_whenPaid_shouldTransitionToCancelled() {
        // Deferred area (conflict #5): refund side-effect not implemented yet.
        // Only assert the state change; do NOT assert refund or stock restore.
        // TODO add refund assertions after conflict #5 is resolved
        OrderContext ctx = new OrderContext("o1", PaidState.INSTANCE);
        ctx.cancel();
        assertThat(ctx.getCurrentState().getStatus()).isEqualTo(OrderStatusEnum.CANCELLED);
    }

    // ---- CANCELLED terminal state ----

    @Test
    void testPay_whenCancelled_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", CancelledState.INSTANCE);
        assertThatThrownBy(ctx::pay)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot pay a cancelled order");
    }

    @Test
    void testComplete_whenCancelled_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", CancelledState.INSTANCE);
        assertThatThrownBy(ctx::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot complete a cancelled order");
    }

    @Test
    void testCancel_whenCancelled_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", CancelledState.INSTANCE);
        assertThatThrownBy(ctx::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    // ---- COMPLETED terminal state ----

    @Test
    void testPay_whenCompleted_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", CompletedState.INSTANCE);
        assertThatThrownBy(ctx::pay)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot pay a completed order");
    }

    @Test
    void testComplete_whenCompleted_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", CompletedState.INSTANCE);
        assertThatThrownBy(ctx::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already completed");
    }

    @Test
    void testCancel_whenCompleted_shouldThrow() {
        OrderContext ctx = new OrderContext("o1", CompletedState.INSTANCE);
        assertThatThrownBy(ctx::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot cancel a completed order");
    }
}
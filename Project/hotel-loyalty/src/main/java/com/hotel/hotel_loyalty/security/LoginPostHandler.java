package com.hotel.hotel_loyalty.security;

/**
 * Abstract handler in the Chain of Responsibility pattern (GoF).
 * <p>
 * Each handler does ONE check, then passes control to the next.
 * Returning false stops the chain (interrupt).
 * <p>
 * Design note: Spring Security's SecurityFilterChain is framework code,
 * not our own pattern implementation. This hand-written chain holds our
 * login business rules (status check, portal match, logging, redirect)
 * so they are visible, testable, and defensible in one place.
 *
 * @author wukewei
 * @since 1.1
 */
public abstract class LoginPostHandler {

    /** Successor in the chain. */
    private LoginPostHandler next;

    /**
     * Links the next handler.
     * Fluent API so the chain can be assembled in one line.
     *
     * @param next the successor handler
     * @return the successor, for chaining
     */
    public LoginPostHandler linkWith(LoginPostHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Template method: run this handler, then the next if passed.
     *
     * @param ctx login context shared by the whole chain
     * @return true if the whole chain passed, false if interrupted
     */
    public final boolean handle(LoginContext ctx) {
        boolean pass = doHandle(ctx);
        if (pass && next != null) {
            return next.handle(ctx);
        }
        return pass;
    }

    /**
     * Each concrete handler implements its single check here.
     *
     * @param ctx login context
     * @return true to continue the chain, false to interrupt
     */
    protected abstract boolean doHandle(LoginContext ctx);
}
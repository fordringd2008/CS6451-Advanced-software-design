package com.hotel.hotel_loyalty.security;

/**
 * Chain node 1: reject non-normal accounts (status != 0).
 * <p>
 * Interrupt type. Placed first because it is the cheapest check
 * (fail fast principle). Both INACTIVE (1) and FORBIDDEN (2)
 * accounts are rejected — only NORMAL (0) may log in.
 *
 * @author wukewei
 * @since 1.1
 */
public class StatusCheckHandler extends LoginPostHandler {

    @Override
    protected boolean doHandle(LoginContext ctx) {
        if (ctx.getStatus() != 0) {
            ctx.setErrorMessage("Account is not active");
            return false;
        }
        return true;
    }
}
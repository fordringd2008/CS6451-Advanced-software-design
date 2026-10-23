package com.hotel.hotel_loyalty.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chain node 3: record a login log.
 * <p>
 * Pass-through type — never interrupts the chain.
 * Placed after status and portal checks so we only log
 * users who actually passed the business rules.
 *
 * @author Fordring
 * @since 1.1
 */
public class LoginLogHandler extends LoginPostHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginLogHandler.class);

    @Override
    protected boolean doHandle(LoginContext ctx) {
        log.info("Login success: uid={}, role={}, portal={}",
                ctx.getUid(), ctx.getRole(), ctx.getPortal());
        return true;
    }
}
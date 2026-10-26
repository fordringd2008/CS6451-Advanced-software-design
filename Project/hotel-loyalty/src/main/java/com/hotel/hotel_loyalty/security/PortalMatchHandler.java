package com.hotel.hotel_loyalty.security;

/**
 * Chain node 2: the user's role must match the login portal.
 * <p>
 * Interrupt type. A guest on /admin/login (or a manager on /login)
 * is rejected with the same message as a bad password, so the
 * login page simply shows "login failed" — it also avoids
 * revealing that the account exists (security best practice,
 * consistent with {@code LoginFailureHandler}).
 *
 * @author wukewei
 * @since 1.1
 */
public class PortalMatchHandler extends LoginPostHandler {

    @Override
    protected boolean doHandle(LoginContext ctx) {
        boolean isAdminPortal = "admin".equals(ctx.getPortal());
        boolean isManager = ctx.getRole() == 1;
        if (isAdminPortal != isManager) {
            ctx.setErrorMessage("Invalid account or password");
            return false;
        }
        return true;
    }
}
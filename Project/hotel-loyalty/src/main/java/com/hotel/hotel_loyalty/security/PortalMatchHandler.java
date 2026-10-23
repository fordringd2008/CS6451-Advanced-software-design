package com.hotel.hotel_loyalty.security;

/**
 * Chain node 2: the user's role must match the login portal.
 * <p>
 * Interrupt type. A guest on /admin/login (or a manager on /login)
 * gets a clear error message instead of a silent redirect.
 * This improves UX and adds defense in depth — the URL rules
 * in SecurityConfig would block them anyway, but here they
 * learn the reason immediately.
 *
 * @author Fordring
 * @since 1.1
 */
public class PortalMatchHandler extends LoginPostHandler {

    @Override
    protected boolean doHandle(LoginContext ctx) {
        boolean isAdminPortal = "admin".equals(ctx.getPortal());
        boolean isManager = ctx.getRole() == 1;
        if (isAdminPortal != isManager) {
            ctx.setErrorMessage("Wrong login portal for this role");
            return false;
        }
        return true;
    }
}
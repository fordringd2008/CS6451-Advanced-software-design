package com.hotel.hotel_loyalty.security;

/**
 * Chain node 4 (terminal): decide where to go after login.
 * <p>
 * Why last: redirect is the final action; it needs all
 * earlier checks to have passed.
 *
 * @author Fordring
 * @since 1.1
 */
public class RedirectHandler extends LoginPostHandler {

    @Override
    protected boolean doHandle(LoginContext ctx) {
        if (ctx.getRole() == 1) {
            ctx.setRedirectUrl("/admin/products");
        } else {
            ctx.setRedirectUrl("/guest/" + ctx.getUid() + "/my-points");
        }
        return true;
    }
}
package com.hotel.hotel_loyalty.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Sends the user to the login page matching their role after logout:
 * manager → {@code /admin/login}, guest → {@code /login}.
 * <p>
 * Without this, {@code logoutSuccessUrl("/login")} always lands on the
 * guest portal, so an admin who signs out would see the guest login page.
 *
 * @author Fordring
 * @since 1.1
 */
@Component
public class PortalAwareLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        String contextPath = request.getContextPath();
        String target = "/login";
        if (authentication != null
                && authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            target = "/admin/login";
        }
        response.sendRedirect(contextPath + target);
    }
}
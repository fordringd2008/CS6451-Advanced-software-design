package com.hotel.hotel_loyalty.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles failed login (wrong password, user not found).
 * <p>
 * Sends the user back to the matching login page with a clear
 * error message. Does NOT reveal whether the account exists
 * (security best practice: same message for both cases).
 *
 * @author Fordring
 * @since 1.1
 */
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        String portal = request.getParameter("portal");
        String loginPage = "admin".equals(portal) ? "/admin/login" : "/login";

        // Same message for wrong password and unknown user on purpose
        String message = "Invalid account or password";

        response.sendRedirect(loginPage + "?error="
                + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
package com.hotel.hotel_loyalty.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Directs unauthenticated users to the login page matching the portal
 * they tried to reach: {@code /admin/**} → {@code /admin/login},
 * everything else → {@code /login}.
 * <p>
 * Without this, Spring Security always sends unauthenticated users to
 * the single {@code formLogin().loginPage("/login")}, so an admin who
 * bookmarks {@code /admin/products} would land on the guest login page.
 *
 * @author wukewei
 * @since 1.1
 */
@Component
public class PortalAwareAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI().substring(contextPath.length());
        if (uri.startsWith("/admin") || uri.startsWith("/api/admin")) {
            response.sendRedirect(contextPath + "/admin/login");
        } else {
            response.sendRedirect(contextPath + "/login");
        }
    }
}
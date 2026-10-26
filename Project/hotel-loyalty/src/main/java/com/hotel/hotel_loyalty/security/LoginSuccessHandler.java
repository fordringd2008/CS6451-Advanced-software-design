package com.hotel.hotel_loyalty.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Bridge between Spring Security and our hand-written CoR.
 * <p>
 * The framework already verified the password (authentication).
 * Our chain runs business rules AFTER successful authentication:
 * status check, portal match, login log, redirect decision.
 * <p>
 * Design note: business rules live here, not in the framework,
 * so the whole login logic is visible in our own code.
 *
 * @author wukewei
 * @since 1.1
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException {
        // Read the hidden "portal" field from the login form
        String portal = request.getParameter("portal");

        // Build context from the authenticated principal
        AppUserDetailsService.AppUserDetails details =
                (AppUserDetailsService.AppUserDetails) authentication.getPrincipal();
        LoginContext ctx = new LoginContext(
                details.getUsername(), details.getDbRole(),
                details.getDbStatus(), portal);

        // Run the chain: Status -> Portal -> Log -> Redirect
        boolean passed = buildChain().handle(ctx);
        if (passed) {
            response.sendRedirect(ctx.getRedirectUrl());
        } else {
            // Business rule rejected. Auth already passed, so we
            // must invalidate the session before sending back.
            request.getSession().invalidate();
            String loginPage = "admin".equals(portal) ? "/admin/login" : "/login";
            response.sendRedirect(loginPage + "?error="
                    + URLEncoder.encode(ctx.getErrorMessage(), StandardCharsets.UTF_8));
        }
    }

    /**
     * Assembles the chain: Status -> Portal -> Log -> Redirect.
     * Order matters: cheapest checks first (fail fast).
     *
     * @return the head of the chain
     */
    private LoginPostHandler buildChain() {
        StatusCheckHandler head = new StatusCheckHandler();
        head.linkWith(new PortalMatchHandler())
            .linkWith(new LoginLogHandler())
            .linkWith(new RedirectHandler());
        return head;
    }
}
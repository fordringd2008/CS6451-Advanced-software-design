package com.hotel.hotel_loyalty.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the full Spring Security chain (session auth + RBAC
 * URL rules + two-portal form login).
 * <p>
 * Uses the real {@code SecurityConfig}, real {@link AppUserDetailsService}
 * (DB role 0/1 → ROLE_GUEST/MANAGER) and the real CoR
 * (Status → Portal → Log → Redirect) against H2 seed data. The demo
 * credentials come from {@code data.sql}: admin/admin123 (manager),
 * guest/guest123 (guest), disabled/guest123 (inactive guest).
 *
 * @author Fordring
 * @since 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private static final String LOGIN_URL = "/auth/login";
    private static final String UNIFIED_ERROR = "Invalid+account+or+password";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("manager logs in via admin portal → redirected to /admin/products")
    void testLogin_whenManagerOnAdminPortal_shouldRedirectToAdminProducts() throws Exception {
        // when: manager authenticates on the admin portal
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", "admin")
                        .param("password", "admin123")
                        .param("portal", "admin"))
                // then: CoR passes, RedirectHandler sends to /admin/products
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    @DisplayName("guest logs in via guest portal → redirected to /guest/{uid}/my-points")
    void testLogin_whenGuestOnGuestPortal_shouldRedirectToMyPoints() throws Exception {
        // when: guest authenticates on the guest portal
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", "guest")
                        .param("password", "guest123")
                        .param("portal", "guest"))
                // then: CoR passes, RedirectHandler sends to /guest/guest/my-points
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guest/guest/my-points"));
    }

    @Test
    @DisplayName("guest on admin portal → CoR PortalMatch rejects, unified error message")
    void testLogin_whenGuestOnAdminPortal_shouldRejectWithUnifiedError() throws Exception {
        // when: guest authenticates correctly but on the wrong portal
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", "guest")
                        .param("password", "guest123")
                        .param("portal", "admin"))
                // then: PortalMatchHandler rejects, session invalidated, unified error
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login?error=" + UNIFIED_ERROR));
    }

    @Test
    @DisplayName("manager on guest portal → CoR PortalMatch rejects, unified error message")
    void testLogin_whenManagerOnGuestPortal_shouldRejectWithUnifiedError() throws Exception {
        // when: manager authenticates correctly but on the wrong portal
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", "admin")
                        .param("password", "admin123")
                        .param("portal", "guest"))
                // then: PortalMatchHandler rejects, session invalidated, unified error
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=" + UNIFIED_ERROR));
    }

    @Test
    @DisplayName("wrong password → LoginFailureHandler shows unified error")
    void testLogin_whenWrongPassword_shouldShowUnifiedError() throws Exception {
        // when: guest submits a wrong password
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", "guest")
                        .param("password", "wrong")
                        .param("portal", "guest"))
                // then: LoginFailureHandler sends back with unified message
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=" + UNIFIED_ERROR));
    }

    @Test
    @DisplayName("disabled user logs in → CoR StatusCheck rejects with status error")
    void testLogin_whenAccountDisabled_shouldRejectWithStatusError() throws Exception {
        // given: 'disabled' user has status=1 (INACTIVE); password is correct
        // when: disabled user authenticates successfully but StatusCheckHandler rejects
        mockMvc.perform(post(LOGIN_URL)
                        .param("username", "disabled")
                        .param("password", "guest123")
                        .param("portal", "guest"))
                // then: chain fails at node 1, session invalidated, error message shown
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=Account+is+not+active"));
    }

    @Test
    @DisplayName("unauthenticated GET /guest/** → PortalAwareEntryPoint redirects to /login")
    void testAccess_whenUnauthenticatedToGuest_shouldRedirectToLogin() throws Exception {
        // when: no session, GET a guest URL
        mockMvc.perform(get("/guest/guest/points-mall"))
                // then: entry point redirects to the guest login page
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("unauthenticated GET /admin/** → PortalAwareEntryPoint redirects to /admin/login")
    void testAccess_whenUnauthenticatedToAdmin_shouldRedirectToAdminLogin() throws Exception {
        // when: no session, GET an admin URL
        mockMvc.perform(get("/admin/products"))
                // then: entry point redirects to the admin login page
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/login"));
    }

    @Test
    @DisplayName("manager session → GET /admin/products renders the admin page")
    void testAccess_whenManagerToAdmin_shouldRenderPage() throws Exception {
        // given: manager has an authenticated session
        MockHttpSession session = loginSession("admin", "admin123", "admin");
        // when: manager GETs the admin product page
        mockMvc.perform(get("/admin/products").session(session))
                // then: RBAC allows, controller forwards to the JSP view
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/admin_products.jsp"));
    }

    @Test
    @DisplayName("guest session → GET /admin/products denied by RBAC URL rule (403)")
    void testAccess_whenGuestToAdmin_shouldForbidden() throws Exception {
        // given: guest has an authenticated session
        MockHttpSession session = loginSession("guest", "guest123", "guest");
        // when: guest GETs an admin URL
        mockMvc.perform(get("/admin/products").session(session))
                // then: RBAC URL rule denies access (403)
                .andExpect(status().isForbidden());
    }

    /**
     * Performs a real form login and returns the authenticated session so
     * subsequent requests carry the SecurityContext.
     */
    private MockHttpSession loginSession(String uid, String password, String portal) throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_URL)
                        .param("username", uid)
                        .param("password", password)
                        .param("portal", portal))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
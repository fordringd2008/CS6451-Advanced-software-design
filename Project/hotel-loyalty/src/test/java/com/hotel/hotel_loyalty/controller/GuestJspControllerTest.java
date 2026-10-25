package com.hotel.hotel_loyalty.controller;

import com.hotel.hotel_loyalty.config.SecurityConfig;
import com.hotel.hotel_loyalty.security.LoginFailureHandler;
import com.hotel.hotel_loyalty.security.LoginSuccessHandler;
import com.hotel.hotel_loyalty.security.PortalAwareAuthenticationEntryPoint;
import com.hotel.hotel_loyalty.security.PortalAwareLogoutSuccessHandler;
import com.hotel.hotel_loyalty.service.GuestPointsService;
import com.hotel.hotel_loyalty.vo.AccountVO;
import com.hotel.hotel_loyalty.vo.MyPointsVO;
import com.hotel.hotel_loyalty.vo.PointsMallVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Web-layer slice tests for {@link GuestJspController} — routing, form
 * handling, and IDOR defense. Real SecurityConfig + RBAC URL rules;
 * GuestPointsService and the four login handlers are Mockito mocks.
 *
 * @author Fordring
 * @since 1.0
 */
@WebMvcTest(controllers = GuestJspController.class)
@Import(SecurityConfig.class)
class GuestJspControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuestPointsService guestPointsService;
    @MockitoBean
    private LoginSuccessHandler loginSuccessHandler;
    @MockitoBean
    private LoginFailureHandler loginFailureHandler;
    @MockitoBean
    private PortalAwareAuthenticationEntryPoint authenticationEntryPoint;
    @MockitoBean
    private PortalAwareLogoutSuccessHandler logoutSuccessHandler;

    @BeforeEach
    void stubQueryMethods() {
        when(guestPointsService.getPointsMall("guest")).thenReturn(new PointsMallVO());
        when(guestPointsService.getMyPoints("guest")).thenReturn(new MyPointsVO());
        when(guestPointsService.getAccount("guest")).thenReturn(new AccountVO());
    }

    @Test
    void testPointsMall_whenOwnerMatches_shouldReturnView() throws Exception {
        mockMvc.perform(get("/guest/guest/points-mall").with(user("guest").roles("GUEST")))
                .andExpect(status().isOk())
                .andExpect(view().name("guest_points_mall"))
                .andExpect(model().attribute("uid", "guest"))
                .andExpect(model().attributeExists("mall"));
    }

    @Test
    void testPointsMall_whenUidMismatch_shouldBeForbidden() throws Exception {
        // IDOR defense: session identity is "guest", URL claims "alice" —
        // valid GUEST role but someone else's resource => 403
        mockMvc.perform(get("/guest/alice/points-mall").with(user("guest").roles("GUEST")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testMyPoints_whenOwnerMatches_shouldReturnView() throws Exception {
        mockMvc.perform(get("/guest/guest/my-points").with(user("guest").roles("GUEST")))
                .andExpect(status().isOk())
                .andExpect(view().name("guest_my_points"))
                .andExpect(model().attributeExists("points"));
    }

    @Test
    void testAccount_whenOwnerMatches_shouldReturnView() throws Exception {
        mockMvc.perform(get("/guest/guest/account").with(user("guest").roles("GUEST")))
                .andExpect(status().isOk())
                .andExpect(view().name("guest_account"))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    void testAccount_whenUidMismatch_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/guest/alice/account").with(user("guest").roles("GUEST")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRedeem_whenSuccessful_shouldRedirectToMyPoints() throws Exception {
        when(guestPointsService.redeem(any())).thenReturn("O-TEST01");
        mockMvc.perform(post("/guest/guest/redemptions").with(user("guest").roles("GUEST"))
                        .param("uid", "guest")
                        .param("pid", "P-003")
                        .param("pmid", "PM-1")
                        .param("pointsUsed", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guest/guest/my-points"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testRedeem_whenBusinessError_shouldRedirectToPointsMall() throws Exception {
        when(guestPointsService.redeem(any()))
                .thenThrow(new IllegalArgumentException("insufficient points"));
        mockMvc.perform(post("/guest/guest/redemptions").with(user("guest").roles("GUEST"))
                        .param("uid", "guest")
                        .param("pid", "P-003")
                        .param("pmid", "PM-1")
                        .param("pointsUsed", "800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guest/guest/points-mall"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testRedeem_whenFormValidationFails_shouldRedirectToPointsMall() throws Exception {
        // @PositiveOrZero rejects -1
        mockMvc.perform(post("/guest/guest/redemptions").with(user("guest").roles("GUEST"))
                        .param("uid", "guest")
                        .param("pid", "P-003")
                        .param("pmid", "PM-1")
                        .param("pointsUsed", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guest/guest/points-mall"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
package com.hotel.hotel_loyalty.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Renders the two login pages.
 * <p>
 * Two portals for defense in depth (entry isolation):
 * <ul>
 *   <li>/login       — guest portal</li>
 *   <li>/admin/login — admin portal</li>
 * </ul>
 * Both forms POST to the same /auth/login URL (Spring Security);
 * a hidden "portal" field tells our CoR which page was used.
 *
 * @author Fordring
 * @since 1.1
 */
@Controller
public class AuthJspController {

    /** Guest login page. */
    @GetMapping("/login")
    public String guestLogin() {
        return "login";
    }

    /** Admin login page. */
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin_login";
    }
}
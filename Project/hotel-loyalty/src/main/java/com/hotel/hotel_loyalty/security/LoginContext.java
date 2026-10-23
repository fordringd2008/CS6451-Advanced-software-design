package com.hotel.hotel_loyalty.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Carries login state through the handler chain (GoF Chain of Responsibility).
 * Handlers read from it; only RedirectHandler writes redirectUrl.
 *
 * @author Fordring
 * @since 1.1
 */
@Getter
@Setter
@RequiredArgsConstructor
public class LoginContext {

    /** Login account (user business key). */
    private final String uid;

    /** DB role encoding: 0 = guest, 1 = manager (see UserRoleEnum). */
    private final int role;

    /** DB account status: 0 = normal, 1 = inactive, 2 = forbidden (see UserStatusEnum). */
    private final int status;

    /** Which login page was used: "guest" or "admin". */
    private final String portal;

    /** Error message set when a handler rejects the login. */
    private String errorMessage;

    /** Target URL set by the terminal RedirectHandler. */
    private String redirectUrl;
}
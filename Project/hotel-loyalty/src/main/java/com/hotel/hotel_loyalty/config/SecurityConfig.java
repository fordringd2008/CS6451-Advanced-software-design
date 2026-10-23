package com.hotel.hotel_loyalty.config;

import com.hotel.hotel_loyalty.security.LoginFailureHandler;
import com.hotel.hotel_loyalty.security.LoginSuccessHandler;
import com.hotel.hotel_loyalty.security.PortalAwareAuthenticationEntryPoint;
import com.hotel.hotel_loyalty.security.PortalAwareLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

/**
 * Spring Security configuration.
 * <p>
 * Strategy: Session-based authentication + RBAC URL rules
 * (NIST standard) + BCrypt password encoding (OWASP recommended).
 * <p>
 * Two login portals (defense in depth):
 * <ul>
 *   <li>/login       — guest portal  (ROLE_GUEST)</li>
 *   <li>/admin/login — admin portal  (ROLE_MANAGER)</li>
 * </ul>
 * <p>
 * Evolution seam: to switch to OAuth2/OIDC later, replace the
 * formLogin block and AppUserDetailsService — URL rules and all
 * business code stay unchanged (Evolutionary Architecture).
 *
 * @author Fordring
 * @since 1.1
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final PortalAwareAuthenticationEntryPoint authenticationEntryPoint;
    private final PortalAwareLogoutSuccessHandler logoutSuccessHandler;

    public SecurityConfig(LoginSuccessHandler loginSuccessHandler,
                          LoginFailureHandler loginFailureHandler,
                          PortalAwareAuthenticationEntryPoint authenticationEntryPoint,
                          PortalAwareLogoutSuccessHandler logoutSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    /**
     * BCrypt encoder (OWASP recommended).
     * Strategy pattern in action: PasswordEncoder is the interface,
     * BCryptPasswordEncoder is the concrete strategy.
     * Slow hash + built-in salt defeats brute force and rainbow tables.
     *
     * @return the password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // default cost = 10
    }

    /**
     * RBAC URL rules + form login + logout.
     * <p>
     * CSRF is disabled because the existing JSP pages call REST APIs
     * via fetch without CSRF tokens. Academic project simplification.
     * Production fix: enable CSRF and inject the token into JSP forms.
     *
     * @param http the security builder
     * @return the security filter chain
     * @throws Exception if the chain cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // JSP rendering uses FORWARD dispatch; Spring Boot errors use ERROR dispatch.
                // Without this, the AuthorizationFilter blocks the FORWARD to /WEB-INF/jsp/**
                // and causes an infinite redirect loop on the login page.
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers("/login", "/admin/login",
                        "/css/**", "/swagger-ui/**", "/v3/api-docs/**",
                        "/h2-console/**").permitAll()
                .requestMatchers("/api/admin/**", "/admin/**").hasRole("MANAGER")
                .requestMatchers("/guest/**", "/api/guest/**").hasRole("GUEST")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/auth/login")   // both forms POST here
                .successHandler(loginSuccessHandler) // triggers our CoR
                .failureHandler(loginFailureHandler) // sends back with error msg
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                // Manager → /admin/login, guest → /login (role-aware)
                .logoutSuccessHandler(logoutSuccessHandler))
            // Unauthenticated users get the login page matching the portal
            // they tried to reach (/admin/** → /admin/login, else → /login).
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint(authenticationEntryPoint))
            .csrf(csrf -> csrf.disable())
            // H2 console uses an iframe; allow same-origin frames
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }
}
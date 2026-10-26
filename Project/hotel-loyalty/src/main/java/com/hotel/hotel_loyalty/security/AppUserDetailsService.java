package com.hotel.hotel_loyalty.security;

import com.hotel.hotel_loyalty.domain.UserDO;
import com.hotel.hotel_loyalty.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Loads user from DB and maps to Spring Security UserDetails.
 * <p>
 * This is the ONLY class that knows the DB role encoding (0/1).
 * All other code reads roles from Spring Security authorities.
 * <p>
 * Evolution seam: to integrate with a hotel-wide login system later,
 * replace this class and the AuthenticationProvider — controllers
 * and services stay untouched because they read identity from
 * SecurityContext, never from HttpSession directly.
 *
 * @author wukewei
 * @since 1.1
 */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public AppUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * Maps a DB row to Spring Security UserDetails.
     * <p>
     * Note: status check is NOT done here on purpose.
     * It lives in our hand-written CoR so all login rules
     * stay in one visible place.
     *
     * @param uid user business key (login account)
     * @return UserDetails with role authority and DB status/role
     * @throws UsernameNotFoundException if the uid does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String uid) {
        UserDO user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + uid);
        }
        String role = user.getRole() == 1 ? "MANAGER" : "GUEST";
        return new AppUserDetails(user.getUid(), user.getPassword(),
                role, user.getStatus(), user.getRole());
    }

    /**
     * Custom UserDetails that also carries DB status and role,
     * so the CoR can read them without a second DB query.
     */
    public static class AppUserDetails extends User {

        private final int dbStatus;
        private final int dbRole;

        /**
         * Creates UserDetails with a single ROLE_ authority.
         *
         * @param uid      login account
         * @param password BCrypt hash from DB
         * @param role     "GUEST" or "MANAGER"
         * @param dbStatus DB status encoding: 0/1/2
         * @param dbRole   DB role encoding: 0/1
         */
        public AppUserDetails(String uid, String password, String role,
                              int dbStatus, int dbRole) {
            super(uid, password, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            this.dbStatus = dbStatus;
            this.dbRole = dbRole;
        }

        public int getDbStatus() {
            return dbStatus;
        }

        public int getDbRole() {
            return dbRole;
        }
    }
}
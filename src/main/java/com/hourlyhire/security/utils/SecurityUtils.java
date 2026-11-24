package com.hourlyhire.security.utils;

import com.hourlyhire.authentication.enums.Role;
import com.hourlyhire.security.jwt.AuthenticatedUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /** The logged-in user, as built from the access token by JwtAuthenticationFilter. */
    public static AuthenticatedUser getCurrentUser() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found.");
        }
        Object principal = getAuthentication().getPrincipal();
        if (!(principal instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("Invalid authenticated principal.");
        }
        return user;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().id();
    }

    public static Role getCurrentRole() {
        return getCurrentUser().role();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().username();
    }

    public static String getCurrentEmail() {
        return getCurrentUser().email();
    }

    public static boolean hasRole(Role role) {
        return isAuthenticated() && getCurrentRole() == role;
    }
}
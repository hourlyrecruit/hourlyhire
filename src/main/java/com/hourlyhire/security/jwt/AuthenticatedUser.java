package com.hourlyhire.security.jwt;

import com.hourlyhire.authentication.enums.Role;

/** The logged-in user as seen by controllers, built from the access token. */
public record AuthenticatedUser(Long id, Role role) {
}
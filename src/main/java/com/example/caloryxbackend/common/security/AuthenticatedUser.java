package com.example.caloryxbackend.common.security;

public record AuthenticatedUser(
        String auth0Id,
        String email
) {
}

package com.example.caloriexbackend.common.security;

public record AuthenticatedUser(
        String auth0Id,
        String email
) {
}

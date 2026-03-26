package com.example.caloriexbackend.auth.payload;

import java.util.UUID;

public record AuthMeResponse(
        boolean authenticated,
        UUID userId,
        String auth0Id,
        String email,
        boolean emailVerified,
        String fullName,
        String role,
        boolean hasProfile
) {
}

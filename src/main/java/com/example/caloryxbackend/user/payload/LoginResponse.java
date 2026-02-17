package com.example.caloryxbackend.user.payload;

public record LoginResponse(
        String token,
        long expiresInSeconds
) {}


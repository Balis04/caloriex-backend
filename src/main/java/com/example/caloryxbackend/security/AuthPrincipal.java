package com.example.caloryxbackend.security;


import java.util.UUID;

public record AuthPrincipal(UUID userId, String email, String role) {
}

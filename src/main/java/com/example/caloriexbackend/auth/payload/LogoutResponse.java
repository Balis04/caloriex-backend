package com.example.caloriexbackend.auth.payload;

public record LogoutResponse(
        boolean loggedOut,
        String auth0LogoutUrl
) {
}

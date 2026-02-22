package com.example.caloryxbackend.account;

public record CurrentUser(
        String auth0Id,
        String email
) {
}

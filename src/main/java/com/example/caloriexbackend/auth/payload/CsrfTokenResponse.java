package com.example.caloriexbackend.auth.payload;

public record CsrfTokenResponse(
        String headerName,
        String parameterName,
        String token
) {
}

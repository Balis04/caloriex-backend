package com.example.caloriexbackend.auth.controller;

import com.example.caloriexbackend.auth.payload.AuthMeResponse;
import com.example.caloriexbackend.auth.payload.CsrfTokenResponse;
import com.example.caloriexbackend.auth.payload.LogoutResponse;
import com.example.caloriexbackend.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/auth0"))
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @GetMapping("/csrf")
    public ResponseEntity<CsrfTokenResponse> csrf(CsrfToken csrfToken) {
        return ResponseEntity.ok(new CsrfTokenResponse(
                csrfToken.getHeaderName(),
                csrfToken.getParameterName(),
                csrfToken.getToken()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Authentication authentication) {
        authService.logout(request, response, authentication);
        return ResponseEntity.ok(new LogoutResponse(true, authService.getAuth0LogoutUrl()));
    }
}

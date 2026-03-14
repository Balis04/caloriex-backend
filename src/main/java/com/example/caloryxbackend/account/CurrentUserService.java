package com.example.caloryxbackend.account;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrentUserService {

    private static final List<String> EMAIL_CLAIM_CANDIDATES = List.of(
            "email",
            "preferred_username",
            "upn",
            "unique_name"
    );

    public CurrentUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("https://caloriex.com/email");

        return new CurrentUser(auth0Id, email);
    }

    public String getAuth0Id() {
        return get().auth0Id();
    }

    public String getEmail() {
        return get().email();
    }
}

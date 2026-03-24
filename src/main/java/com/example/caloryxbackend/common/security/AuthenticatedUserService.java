package com.example.caloryxbackend.common.security;

import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    public User getUser() {
        String auth0Id = getJwt().getSubject();

        return userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public AuthenticatedUser getAuthenticatedUser() {
        Jwt jwt = getJwt();

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaimAsString("https://caloriex.com/email");

        return new AuthenticatedUser(auth0Id, email);
    }

    public String getAuth0Id() {
        return getAuthenticatedUser().auth0Id();
    }

    public String getEmail() {
        return getAuthenticatedUser().email();
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        return jwt;
    }
}

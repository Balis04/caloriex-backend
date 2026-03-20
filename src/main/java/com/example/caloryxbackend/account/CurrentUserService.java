package com.example.caloryxbackend.account;

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
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No JWT authentication found");
        }

        String auth0Id = jwt.getSubject();

        return userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

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

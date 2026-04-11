package com.example.caloriexbackend.common.security;

import com.example.caloriexbackend.common.exception.NotFoundException;
import com.example.caloriexbackend.entities.User;
import com.example.caloriexbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    @Transactional
    public User getUser() {
        AuthenticatedUser authenticatedUser = getAuthenticatedUser();

        return findUser(authenticatedUser)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public Optional<User> findUser() {
        return findAuthenticatedUser().flatMap(this::findUser);
    }

    @Transactional(readOnly = true)
    public Optional<AuthenticatedUser> findAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof OAuth2AuthenticatedPrincipal oauth2Principal)) {
            return Optional.empty();
        }

        String auth0Id = oauth2Principal.getAttribute("sub");
        if (auth0Id == null || auth0Id.isBlank()) {
            return Optional.empty();
        }

        String email = oauth2Principal.getAttribute("email");
        Boolean emailVerified = oauth2Principal.getAttribute("email_verified");
        String fullName = oauth2Principal.getAttribute("name");

        return Optional.of(new AuthenticatedUser(
                auth0Id,
                email,
                Boolean.TRUE.equals(emailVerified),
                fullName
        ));
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser getAuthenticatedUser() {
        return findAuthenticatedUser()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    public String getAuth0Id() {
        return getAuthenticatedUser().auth0Id();
    }

    public String getEmail() {
        return getAuthenticatedUser().email();
    }

    private Optional<User> findUser(AuthenticatedUser authenticatedUser) {
        return userRepository.findByAuth0Id(authenticatedUser.auth0Id());
    }
}

package com.example.caloryxbackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final UserRepository repository;

    public User getOrCreate(Jwt jwt) {

        String auth0Id = jwt.getSubject();
        String email = jwt.getClaim("email");

        return repository.findByAuth0id(auth0Id)
                .orElseGet(() -> {
                    User user = new User();
                    user.setAuth0id(auth0Id);
                    user.setEmail(email);
                    return repository.save(user);
                });
    }
}


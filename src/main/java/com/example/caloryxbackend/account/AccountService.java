package com.example.caloryxbackend.account;

import com.example.caloryxbackend.account.payload.AccountResponse;
import com.example.caloryxbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public AccountResponse getNeedRegister() {
        String auth0Id = currentUserService.getAuth0Id();

        boolean exists = userRepository.existsByAuth0id(auth0Id);

        System.out.println("User with auth0Id " + auth0Id + " exists: " + exists);

        // Most: ha nincs DB user → needsRegister = true
        return new AccountResponse(exists);
    }
}
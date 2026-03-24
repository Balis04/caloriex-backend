package com.example.caloryxbackend.account;

import com.example.caloryxbackend.account.payload.response.AccountStatusResponse;
import com.example.caloryxbackend.common.security.AuthenticatedUserService;
import com.example.caloryxbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountStatusService {

    private final AuthenticatedUserService authenticatedUserService;
    private final UserRepository userRepository;

    public AccountStatusResponse getProfileStatus() {
        String auth0Id = authenticatedUserService.getAuth0Id();

        boolean exists = userRepository.existsByAuth0id(auth0Id);

        return new AccountStatusResponse(exists);
    }
}

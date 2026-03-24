package com.example.caloriexbackend.account;

import com.example.caloriexbackend.account.payload.response.AccountStatusResponse;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import com.example.caloriexbackend.user.UserRepository;
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

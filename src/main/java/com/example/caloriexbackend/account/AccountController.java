package com.example.caloriexbackend.account;

import com.example.caloriexbackend.account.payload.response.AccountStatusResponse;
import com.example.caloriexbackend.common.security.AuthenticatedUser;
import com.example.caloriexbackend.common.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountStatusService accountStatusService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping("/account/has-profile")
    public ResponseEntity<AccountStatusResponse> getProfileStatus() {
        return ResponseEntity.ok(accountStatusService.getProfileStatus());
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthenticatedUser> getProfile() {
        return ResponseEntity.ok(authenticatedUserService.getAuthenticatedUser());
    }
}

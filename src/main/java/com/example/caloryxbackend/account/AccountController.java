package com.example.caloryxbackend.account;

import com.example.caloryxbackend.account.payload.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account/has-profile")
    public ResponseEntity<AccountResponse> checkRegister() {
        return ResponseEntity.ok(accountService.getHasProfile());
    }
}
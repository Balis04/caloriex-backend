package com.example.caloryxbackend.user;

import com.example.caloryxbackend.account.CurrentUserService;
import com.example.caloryxbackend.user.model.payload.RegisterRequest;
import com.example.caloryxbackend.user.model.payload.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        String auth0Id = currentUserService.getAuth0Id();
        return ResponseEntity.ok(userService.getCurrentUserProfile(auth0Id));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        String auth0Id = currentUserService.getAuth0Id();
        String email = currentUserService.getEmail();
        userService.registerUser(auth0Id, email, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody RegisterRequest request) {
        String auth0Id = currentUserService.getAuth0Id();
        String email = currentUserService.getEmail();
        return ResponseEntity.ok(userService.updateUser(auth0Id, email, request));
    }
}

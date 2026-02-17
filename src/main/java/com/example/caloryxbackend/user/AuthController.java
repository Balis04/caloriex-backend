package com.example.caloryxbackend.user;

import com.example.caloryxbackend.security.AuthPrincipal;
import com.example.caloryxbackend.user.payload.LoginRequest;
import com.example.caloryxbackend.user.payload.LoginResponse;
import com.example.caloryxbackend.user.payload.RegisterRequest;
import com.example.caloryxbackend.user.payload.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {

        AuthPrincipal principal = (AuthPrincipal) authentication.getPrincipal();

        User user = userService.findUserById(principal.userId());

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getGoal(),
                user.getActivityLevel(),
                user.getStartWeightKg(),
                user.getTargetWeightKg(),
                user.getActualWeightKg(),
                user.getWeeklyGoalKg()
        );

        return ResponseEntity.ok(response);
    }


}

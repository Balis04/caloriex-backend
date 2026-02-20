package com.example.caloryxbackend.user;

import com.example.caloryxbackend.user.payload.OnboardingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final UserProvisioningService provisioningService;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal Jwt jwt) {

        String auth0Id = jwt.getSubject();

        Optional<User> userOpt = userRepository.findByAuth0id(auth0Id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(userOpt.get());
    }


    @PostMapping("/onboarding")
    public ResponseEntity<?> completeOnboarding(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody OnboardingRequest request
    ) {

        String auth0Id = jwt.getSubject();

        if (userRepository.findByAuth0id(auth0Id).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = new User();

        user.setAuth0id(auth0Id);
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setStartWeightKg(request.getStartWeightKg());
        user.setActualWeightKg(request.getStartWeightKg());
        user.setTargetWeightKg(request.getTargetWeightKg());
        user.setWeeklyGoalKg(request.getWeeklyGoalKg());
        user.setGoal(request.getGoal());
        user.setActivityLevel(request.getActivityLevel());
        user.setProfileCompleted(true);

        userRepository.save(user);

        System.out.println("Mentes sikeres");

        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody OnboardingRequest request
    ) {
        String auth0Id = jwt.getSubject();

        User user = userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setActualWeightKg(request.getStartWeightKg());
        user.setTargetWeightKg(request.getTargetWeightKg());
        user.setWeeklyGoalKg(request.getWeeklyGoalKg());
        user.setGoal(request.getGoal());
        user.setActivityLevel(request.getActivityLevel());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }



}

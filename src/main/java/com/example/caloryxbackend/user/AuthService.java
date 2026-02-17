package com.example.caloryxbackend.user;

import com.example.caloryxbackend.security.JwtService;
import com.example.caloryxbackend.user.payload.LoginRequest;
import com.example.caloryxbackend.user.payload.LoginResponse;
import com.example.caloryxbackend.user.payload.RegisterRequest;
import com.example.caloryxbackend.user.payload.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request){
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("EMAIL_ALREADY_EXISTS");
        }
        if (request.targetWeightKg() <= 0 || request.startWeightKg() <= 0) {
            throw new IllegalArgumentException("INVALID_WEIGHT");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setGoal(request.goal());
        user.setActivityLevel(request.activityLevel());
        user.setStartWeightKg(request.startWeightKg());
        user.setActualWeightKg(request.startWeightKg());
        user.setTargetWeightKg(request.targetWeightKg());
        user.setWeeklyGoalKg(request.weeklyGoalKg());

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getRole(),
                saved.getGoal(),
                saved.getActivityLevel(),
                saved.getStartWeightKg(),
                saved.getTargetWeightKg(),
                saved.getActualWeightKg(),
                saved.getWeeklyGoalKg()
        );
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (user.getPassword() == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        long expiresInSeconds = jwtService.getExpirationMinutes() * 60;

        return new LoginResponse(token, expiresInSeconds);
    }

}

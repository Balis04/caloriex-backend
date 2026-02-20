package com.example.caloryxbackend.user;

import com.example.caloryxbackend.user.payload.OnboardingRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public void completeOnboarding(String auth0Id, OnboardingRequest request) {

        User user = userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setStartWeightKg(request.getStartWeightKg());
        user.setActualWeightKg(request.getStartWeightKg());
        user.setTargetWeightKg(request.getTargetWeightKg());
        user.setWeeklyGoalKg(request.getWeeklyGoalKg());
        user.setHeightCm(request.getHeightCm());
        user.setGoal(request.getGoal());
        user.setActivityLevel(request.getActivityLevel());

        user.setProfileCompleted(true);
    }

}

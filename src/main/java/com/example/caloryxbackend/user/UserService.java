package com.example.caloryxbackend.user;

import com.example.caloryxbackend.common.exception.NotFoundException;
import com.example.caloryxbackend.entities.User;
import com.example.caloryxbackend.user.model.payload.RegisterRequest;
import com.example.caloryxbackend.user.model.payload.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUserProfile(String auth0Id) throws NotFoundException {
        User user = userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return mapToProfileResponse(user);
    }

    public void registerUser(String auth0Id, String email, RegisterRequest request) {

        if (userRepository.findByAuth0id(auth0Id).isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        User user = new User();

        user.setAuth0id(auth0Id);
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        user.setFullName(request.getFullName());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());
        user.setRole(request.getRole());
        user.setHeightCm(request.getHeightCm());
        user.setStartWeightKg(request.getStartWeightKg());
        user.setActualWeightKg(request.getStartWeightKg());
        user.setTargetWeightKg(request.getTargetWeightKg());
        user.setWeeklyGoalKg(request.getWeeklyGoalKg());
        user.setActivityLevel(request.getActivityLevel());
        user.setGoal(request.getGoal());

        userRepository.save(user);
    }

    public UserResponse updateUser(String auth0Id, String email, RegisterRequest request) throws NotFoundException{

        User user = userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        user.setFullName(request.getFullName());
        user.setBirthDate(request.getBirthDate());
        user.setGender(request.getGender());
        user.setRole(request.getRole());
        user.setHeightCm(request.getHeightCm());
        user.setStartWeightKg(request.getStartWeightKg());
        user.setActualWeightKg(request.getActualWeightKg());
        user.setTargetWeightKg(request.getTargetWeightKg());
        user.setWeeklyGoalKg(request.getWeeklyGoalKg());
        user.setGoal(request.getGoal());
        user.setActivityLevel(request.getActivityLevel());
        user.setWeeklyGoalKg(request.getWeeklyGoalKg());

        userRepository.save(user);

        return mapToProfileResponse(user);
    }

    private UserResponse mapToProfileResponse(User user) {
        return UserResponse.builder()
                .fullName(user.getFullName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender())
                .role(user.getRole())
                .heightCm(user.getHeightCm())
                .startWeightKg(user.getStartWeightKg())
                .actualWeightKg(user.getActualWeightKg())
                .targetWeightKg(user.getTargetWeightKg())
                .weeklyGoalKg(user.getWeeklyGoalKg())
                .activityLevel(user.getActivityLevel())
                .goal(user.getGoal())
                .build();
    }

    public User findUser(String auth0Id){
        return userRepository.findByAuth0id(auth0Id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}

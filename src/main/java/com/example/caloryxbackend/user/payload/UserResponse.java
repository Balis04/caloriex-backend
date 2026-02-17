package com.example.caloryxbackend.user.payload;

import com.example.caloryxbackend.user.enums.ActivityLevel;
import com.example.caloryxbackend.user.enums.GoalType;
import com.example.caloryxbackend.user.enums.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        GoalType goal,
        ActivityLevel activityLevel,
        Double startWeightKg,
        Double targetWeightKg,
        Double currentWeightKg,
        Double weeklyGoalKg
) {}

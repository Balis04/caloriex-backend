package com.example.caloryxbackend.user.payload;

import com.example.caloryxbackend.user.enums.ActivityLevel;
import com.example.caloryxbackend.user.enums.GoalType;
import com.example.caloryxbackend.user.enums.UserRole;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotNull UserRole role,
        @NotNull GoalType goal,
        @NotNull ActivityLevel activityLevel,
        @NotNull @Positive Double startWeightKg,
        @NotNull @Positive Double targetWeightKg,
        @DecimalMin(value = "0.0", inclusive = false) Double weeklyGoalKg
) {}

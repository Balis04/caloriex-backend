package com.example.caloriexbackend.user.payload.request;

import com.example.caloriexbackend.common.enums.ActivityLevel;
import com.example.caloriexbackend.common.enums.Gender;
import com.example.caloriexbackend.common.enums.GoalType;
import com.example.caloriexbackend.common.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "User role is required")
    private UserRole role;

    @NotNull(message = "Height is required")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must not exceed 300 cm")
    private Integer heightCm;

    @NotNull(message = "Start weight is required")
    @Positive(message = "Start weight must be greater than 0")
    @DecimalMax(value = "500.0", message = "Start weight must not exceed 500 kg")
    private Double startWeightKg;

    @NotNull(message = "Actual weight is required")
    @Positive(message = "Actual weight must be greater than 0")
    @DecimalMax(value = "500.0", message = "Actual weight must not exceed 500 kg")
    private Double actualWeightKg;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    @NotNull(message = "Weekly goal is required")
    private GoalType goal;

    @Positive(message = "Target weight must be greater than 0")
    @DecimalMax(value = "500.0", message = "Target weight must not exceed 500 kg")
    @NotNull(message = "Target weight is required")
    private Double targetWeightKg;

    @Positive(message = "Weekly goal must be greater than 0")
    @DecimalMax(value = "5.0", message = "Weekly goal must not exceed 5 kg")
    @NotNull(message = "Target weight is required")
    private Double weeklyGoalKg;
}

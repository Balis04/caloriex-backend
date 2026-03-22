package com.example.caloryxbackend.user.model.payload;

import com.example.caloryxbackend.common.enums.ActivityLevel;
import com.example.caloryxbackend.common.enums.Gender;
import com.example.caloryxbackend.common.enums.GoalType;
import com.example.caloryxbackend.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {

    private String fullName;
    private LocalDate birthDate;
    private Gender gender;
    private UserRole role;

    private Integer heightCm;

    private Double startWeightKg;
    private Double actualWeightKg;
    private Double targetWeightKg;
    private Double weeklyGoalKg;

    private ActivityLevel activityLevel;
    private GoalType goal;
}

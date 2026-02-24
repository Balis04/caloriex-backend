package com.example.caloryxbackend.user.model.payload;

import com.example.caloryxbackend.user.model.enums.ActivityLevel;
import com.example.caloryxbackend.user.model.enums.Gender;
import com.example.caloryxbackend.user.model.enums.GoalType;
import com.example.caloryxbackend.user.model.enums.UserRole;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    private String fullName;
    private LocalDate birthDate;
    private Gender gender;
    private UserRole role;
    private Integer heightCm;
    private Double startWeightKg;
    private Double actualWeightKg;
    private ActivityLevel activityLevel;
    private GoalType goal;
    private Double targetWeightKg;
    private Double weeklyGoalKg;
}


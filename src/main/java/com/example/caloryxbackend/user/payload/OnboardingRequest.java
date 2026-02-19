package com.example.caloryxbackend.user.payload;

import com.example.caloryxbackend.user.enums.ActivityLevel;
import com.example.caloryxbackend.user.enums.GoalType;
import lombok.Data;

@Data
public class OnboardingRequest {

    private String fullName;
    private String email;
    private Double startWeightKg;
    private Double targetWeightKg;
    private Double weeklyGoalKg;
    private Integer heightCm;
    private GoalType goal;
    private ActivityLevel activityLevel;
}


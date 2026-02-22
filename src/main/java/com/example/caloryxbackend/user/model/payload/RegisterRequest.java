package com.example.caloryxbackend.user.model.payload;

import com.example.caloryxbackend.user.model.enums.ActivityLevel;
import com.example.caloryxbackend.user.model.enums.Gender;
import com.example.caloryxbackend.user.model.enums.GoalType;
import com.example.caloryxbackend.user.model.enums.UserRole;
import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequest {

    private String fullName;
    private Date birthDate;
    private Gender gender;
    private UserRole role;
    private Integer heightCm;
    private Double startWeightKg;
    private ActivityLevel activityLevel;
    private GoalType goal;
    private Double targetWeightKg;
    private Double weeklyGoalKg;
}


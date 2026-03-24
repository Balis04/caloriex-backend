package com.example.caloriexbackend.trainingrequest.email;

import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.common.enums.ActivityLevel;
import com.example.caloriexbackend.common.enums.GoalType;

public final class EmailFormatUtils {

    private EmailFormatUtils() {
    }

    public static String safe(String value) {
        if (value == null) {
            return "not provided";
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? "not provided" : trimmed;
    }

    public static String weight(Double weight) {
        return weight == null ? "not provided" : weight + " kg";
    }

    public static String goal(GoalType goal) {
        if (goal == null) {
            return "not provided";
        }

        return switch (goal) {
            case CUT -> "Weight loss";
            case MAINTAIN -> "Weight maintenance";
            case BULK -> "Muscle gain";
        };
    }

    public static String activityLevel(ActivityLevel activityLevel) {
        if (activityLevel == null) {
            return "not provided";
        }

        return switch (activityLevel) {
            case SEDENTARY -> "Sedentary";
            case LIGHT -> "Light activity";
            case MODERATE -> "Moderate activity";
            case ACTIVE -> "High activity";
        };
    }

    public static String status(TrainingRequestStatus status) {
        if (status == null) {
            return "not provided";
        }

        return switch (status) {
            case PENDING -> "Pending";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
            case CLOSED -> "Closed";
        };
    }
}
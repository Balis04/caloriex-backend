package com.example.caloriexbackend.common.email;

import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.common.enums.ActivityLevel;
import com.example.caloriexbackend.common.enums.GoalType;

public final class EmailFormatUtils {

    private EmailFormatUtils() {
    }

    public static String safe(String value) {
        if (value == null) {
            return "nincs megadva";
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? "nincs megadva" : trimmed;
    }

    public static String weight(Double weight) {
        return weight == null ? "nincs megadva" : weight + " kg";
    }

    public static String goal(GoalType goal) {
        if (goal == null) {
            return "nincs megadva";
        }

        return switch (goal) {
            case CUT -> "Fogyás";
            case MAINTAIN -> "Súlytartás";
            case BULK -> "Tömegnövelés";
        };
    }

    public static String activityLevel(ActivityLevel activityLevel) {
        if (activityLevel == null) {
            return "nincs megadva";
        }

        return switch (activityLevel) {
            case SEDENTARY -> "Ülő életmód";
            case LIGHT -> "Könnyű aktivitás";
            case MODERATE -> "Közepes aktivitás";
            case ACTIVE -> "Magas aktivitás";
        };
    }

    public static String status(TrainingRequestStatus status) {
        if (status == null) {
            return "nincs megadva";
        }

        return switch (status) {
            case PENDING -> "Folyamatban";
            case APPROVED -> "Elfogadva";
            case REJECTED -> "Elutasítva";
            case CLOSED -> "Lezárva";
        };
    }
}
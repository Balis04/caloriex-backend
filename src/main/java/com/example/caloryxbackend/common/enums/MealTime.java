package com.example.caloryxbackend.common.enums;

import lombok.Getter;

@Getter
public enum MealTime {
    BREAKFAST(0, "Breakfast"),
    LUNCH(1, "Lunch"),
    DINNER(2, "Dinner"),
    SNACK(3, "Snack");

    private final int code;
    private final String displayName;

    MealTime(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
}

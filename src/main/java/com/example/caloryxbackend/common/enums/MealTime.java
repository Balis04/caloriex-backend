package com.example.caloryxbackend.common.enums;

import lombok.Getter;

@Getter
public enum MealTime {
    BREAKFAST(0, "Breakfast", 0.25),
    LUNCH(1, "Lunch", 0.35),
    DINNER(2, "Dinner", 0.30),
    SNACK(3, "Snack", 0.10);

    private final int code;
    private final String displayName;
    private final double ratio;

    MealTime(int code, String displayName, double ratio) {
        this.code = code;
        this.displayName = displayName;
        this.ratio = ratio;
    }
}

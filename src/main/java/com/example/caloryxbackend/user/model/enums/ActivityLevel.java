package com.example.caloryxbackend.user.model.enums;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    SEDENTARY(0, "SEDENTARY"),
    LIGHT(1, "LIGHT"),
    MODERATE(2, "MODERATE"),
    ACTIVE(3, "ACTIVE");

    private final int value;
    private final String name;

    ActivityLevel(int value, String name) {
        this.value = value;
        this.name = name;
    }
}

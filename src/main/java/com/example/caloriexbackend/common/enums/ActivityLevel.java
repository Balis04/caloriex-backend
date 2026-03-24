package com.example.caloriexbackend.common.enums;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    SEDENTARY(0, "SEDENTARY", 1.2),
    LIGHT(1, "LIGHT", 1.375),
    MODERATE(2, "MODERATE", 1.55),
    ACTIVE(3, "ACTIVE", 1.725);

    private final int value;
    private final String name;
    private final double multiplier;

    ActivityLevel(int value, String name, double multiplier) {
        this.value = value;
        this.name = name;
        this.multiplier = multiplier;
    }
}

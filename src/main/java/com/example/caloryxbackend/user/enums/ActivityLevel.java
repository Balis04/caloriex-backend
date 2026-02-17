package com.example.caloryxbackend.user.enums;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    LOW(0, "LOW"),
    MEDIUM(1, "MEDIUM"),
    HIGH(2, "HIGH");

    private final int value;
    private final String name;

    ActivityLevel(int value, String name) {
        this.value = value;
        this.name = name;
    }
}

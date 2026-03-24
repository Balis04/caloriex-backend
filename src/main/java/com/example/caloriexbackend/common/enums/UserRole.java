package com.example.caloriexbackend.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    COACH(0, "COACH"),
    USER(1, "USER");

    private final int value;
    private final String name;

    UserRole(int value, String name) {
        this.value = value;
        this.name = name;
    }
}

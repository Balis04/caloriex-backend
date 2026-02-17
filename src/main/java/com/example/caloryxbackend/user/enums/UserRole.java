package com.example.caloryxbackend.user.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    TRAINER(0, "TRAINER"),
    USER(1, "USER");

    private final int value;
    private final String name;

    UserRole(int value, String name) {
        this.value = value;
        this.name = name;
    }
}

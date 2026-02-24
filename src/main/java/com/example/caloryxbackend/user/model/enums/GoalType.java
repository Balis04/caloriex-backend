package com.example.caloryxbackend.user.model.enums;

import lombok.Getter;

@Getter
public enum GoalType {
    CUT(0, "CUT"),
    MAINTAIN(1, "MAINTAIN"),
    BULK(2, "BULK");

    private final int value;
    private final String name;

    GoalType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}

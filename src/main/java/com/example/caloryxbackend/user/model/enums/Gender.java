package com.example.caloryxbackend.user.model.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE(0, "MALE"),
    FEMALE(1, "FEMALE"),
    OTHER(2, "OTHER");

    private final int value;
    private final String name;

    Gender(int value, String name) {
        this.value = value;
        this.name = name;
    }

}

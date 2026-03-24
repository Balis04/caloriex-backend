package com.example.caloriexbackend.common.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE(0, "MALE", 5),
    FEMALE(1, "FEMALE", -161),
    OTHER(2, "OTHER", -78);

    private final int value;
    private final String name;
    private final int bmrOffset;

    Gender(int value, String name, int bmrOffset) {
        this.value = value;
        this.name = name;
        this.bmrOffset = bmrOffset;
    }

}

package com.example.caloryxbackend.customfood.payload;

import java.util.UUID;

public record CustomFoodResponse(
        UUID id,
        String name,
        Double calories,
        Double protein,
        Double fat,
        Double carbohydrates
) {
}

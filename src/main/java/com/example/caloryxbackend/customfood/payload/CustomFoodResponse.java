package com.example.caloryxbackend.customfood.payload;

public record CustomFoodResponse(
        String name,
        Double calories,
        Double fat,
        Double carbohydrates,
        String auth0Id
) {
}

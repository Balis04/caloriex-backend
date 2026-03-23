package com.example.caloryxbackend.usdafoods.payload;

public record UsdaFoodItemResponse(
        Integer fdcId,
        String name,
        String brand,
        Double calories,
        Double protein,
        Double carbohydrates,
        Double fat,
        Double servingSize,
        String servingUnit
) {}

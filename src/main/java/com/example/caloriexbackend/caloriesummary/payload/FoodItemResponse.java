package com.example.caloriexbackend.caloriesummary.payload;

import com.example.caloriexbackend.common.enums.MealTime;

import java.time.LocalDateTime;
import java.util.UUID;

public record FoodItemResponse(
        UUID id,
        String foodName,
        MealTime mealTime,
        Double amount,
        String unit,
        Double calories,
        Double protein,
        Double carbohydrates,
        Double fat,
        LocalDateTime consumedAt
) {
}

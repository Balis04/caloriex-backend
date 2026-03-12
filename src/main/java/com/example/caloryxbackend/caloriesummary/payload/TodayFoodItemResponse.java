package com.example.caloryxbackend.caloriesummary.payload;

import com.example.caloryxbackend.foodlog.MealTime;

import java.time.LocalDateTime;
import java.util.UUID;

public record TodayFoodItemResponse(
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

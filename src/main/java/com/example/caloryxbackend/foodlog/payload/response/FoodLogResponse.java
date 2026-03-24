package com.example.caloryxbackend.foodlog.payload.response;

import com.example.caloryxbackend.common.enums.MealTime;

import java.time.LocalDateTime;
import java.util.UUID;

public record FoodLogResponse(
        UUID id,
        UUID userId,
        MealTime mealTime,
        String foodName,
        Double amount,
        String unit,
        Double calories,
        Double protein,
        Double carbohydrates,
        Double fat,
        LocalDateTime consumedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UUID updatedBy
) {
}

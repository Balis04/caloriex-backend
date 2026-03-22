package com.example.caloryxbackend.caloriesummary.payload;

import com.example.caloryxbackend.common.enums.MealTime;

import java.util.List;

public record MealTimeGroupResponse(
        MealTime mealTime,
        double targetCalories,
        double targetProteinGrams,
        double targetCarbohydratesGrams,
        double targetFatGrams,
        double consumedCalories,
        double consumedProteinGrams,
        double consumedCarbohydratesGrams,
        double consumedFatGrams,
        List<FoodItemResponse> foods
) {
}

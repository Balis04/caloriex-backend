package com.example.caloriexbackend.caloriesummary.payload;

import com.example.caloriexbackend.common.enums.MealTime;

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

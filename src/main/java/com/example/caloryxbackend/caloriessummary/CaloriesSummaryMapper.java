package com.example.caloryxbackend.caloriessummary;

import com.example.caloryxbackend.caloriessummary.repository.DayIntakeProjection;
import com.example.caloryxbackend.caloriessummary.calculation.DailyMacroTargets;
import com.example.caloryxbackend.caloriessummary.calculation.MealCaloriesBreakdown;
import com.example.caloryxbackend.caloriessummary.calculation.MealMacroTotals;
import com.example.caloryxbackend.caloriessummary.payload.*;
import com.example.caloryxbackend.common.enums.MealTime;
import com.example.caloryxbackend.entities.FoodLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CaloriesSummaryMapper {

    FoodItemResponse toFoodItem(FoodLog foodLog);

    List<FoodItemResponse> toFoodItemList(List<FoodLog> logs);

    @Mapping(target = "targetProteinGrams", source = "dailyMacroTargets.proteinGrams")
    @Mapping(target = "targetCarbohydratesGrams", source = "dailyMacroTargets.carbohydratesGrams")
    @Mapping(target = "targetFatGrams", source = "dailyMacroTargets.fatGrams")

    @Mapping(target = "consumedCalories", source = "intake.calories")
    @Mapping(target = "consumedProteinGrams", source = "intake.protein")
    @Mapping(target = "consumedCarbohydratesGrams", source = "intake.carbohydrates")
    @Mapping(target = "consumedFatGrams", source = "intake.fat")

    @Mapping(target = "targetBreakfastKcal", source = "mealCalories.targetBreakfast")
    @Mapping(target = "targetLunchKcal", source = "mealCalories.targetLunch")
    @Mapping(target = "targetDinnerKcal", source = "mealCalories.targetDinner")
    @Mapping(target = "targetSnackKcal", source = "mealCalories.targetSnack")

    @Mapping(target = "consumedBreakfastKcal", source = "mealCalories.consumedBreakfast")
    @Mapping(target = "consumedLunchKcal", source = "mealCalories.consumedLunch")
    @Mapping(target = "consumedDinnerKcal", source = "mealCalories.consumedDinner")
    @Mapping(target = "consumedSnackKcal", source = "mealCalories.consumedSnack")
    CaloriesSummaryResponse toCaloriesSummaryResponse(LocalDate date, double targetCalories,
                                                      DailyMacroTargets dailyMacroTargets, DayIntakeProjection intake,
                                                      MealCaloriesBreakdown mealCalories);

    @Mapping(target = "consumedCalories", source = "summary.calories")
    @Mapping(target = "consumedProteinGrams", source = "summary.protein")
    @Mapping(target = "consumedCarbohydratesGrams", source = "summary.carbohydrates")
    @Mapping(target = "consumedFatGrams", source = "summary.fat")
    MealTimeGroupResponse toMealTimeGroupResponse(
            MealTime mealTime,
            double targetCalories,
            double targetProtein,
            double targetCarbohydrates,
            double targetFat,
            MealMacroTotals summary,
            List<FoodItemResponse> foods
    );
}
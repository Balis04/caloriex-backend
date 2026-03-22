package com.example.caloryxbackend.caloriessummary;

import com.example.caloryxbackend.caloriessummary.calculation.DailyMacroTargets;
import com.example.caloryxbackend.caloriessummary.calculation.MealCaloriesBreakdown;
import com.example.caloryxbackend.caloriessummary.calculation.MealMacroTotals;
import com.example.caloryxbackend.common.enums.MealTime;
import com.example.caloryxbackend.entities.FoodLog;
import com.example.caloryxbackend.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Component
public class CaloriesCalculator {

    public double calculateTargetCalories(User user) {
        double bmr = calculateBmr(user);
        double tdee = bmr * user.getActivityLevel().getMultiplier();
        double adjusted = tdee + user.getGoal().calculateAdjustment(user.getWeeklyGoalKg());

        return Math.max(0, adjusted);
    }

    public DailyMacroTargets calculateMacros(User user, double calories) {
        return user.getGoal().calculateMacros(calories);
    }

    private double calculateBmr(User user) {
        double weight = user.getActualWeightKg();
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        double base = (10 * weight) + (6.25 * user.getHeightCm()) - (5 * age);

        return base + user.getGender().getBmrOffset();
    }

    public MealMacroTotals calculateMealMacros(List<FoodLog> logs) {
        double calories = logs.stream()
                .map(FoodLog::getCalories)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double protein = logs.stream()
                .map(FoodLog::getProtein)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double carbs = logs.stream()
                .map(FoodLog::getCarbohydrates)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double fat = logs.stream()
                .map(FoodLog::getFat)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        return new MealMacroTotals(calories, protein, carbs, fat);
    }

    public MealCaloriesBreakdown calculateMealCalories(double targetCalories, List<FoodLog> logs){

        double targetBreakfast = targetCalories * MealTime.BREAKFAST.getRatio();
        double targetLunch = targetCalories * MealTime.LUNCH.getRatio();
        double targetDinner = targetCalories * MealTime.DINNER.getRatio();
        double targetSnack = targetCalories * MealTime.SNACK.getRatio();

        double consumedBreakfast = sumConsumedCaloriesByMeal(logs, MealTime.BREAKFAST);
        double consumedLunch = sumConsumedCaloriesByMeal(logs, MealTime.LUNCH);
        double consumedDinner = sumConsumedCaloriesByMeal(logs, MealTime.DINNER);
        double consumedSnack = sumConsumedCaloriesByMeal(logs, MealTime.SNACK);

        return new MealCaloriesBreakdown(
                targetBreakfast,
                targetLunch,
                targetDinner,
                targetSnack,
                consumedBreakfast,
                consumedLunch,
                consumedDinner,
                consumedSnack
        );
    }

    private double sumConsumedCaloriesByMeal(List<FoodLog> logs, MealTime mealTime) {
        return logs.stream()
                .filter(log -> mealTime.equals(log.getMealTime()))
                .map(FoodLog::getCalories)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}

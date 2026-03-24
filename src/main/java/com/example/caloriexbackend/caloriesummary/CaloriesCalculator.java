package com.example.caloriexbackend.caloriesummary;

import com.example.caloriexbackend.caloriesummary.calculation.DailyMacroTargets;
import com.example.caloriexbackend.caloriesummary.calculation.MealCaloriesBreakdown;
import com.example.caloriexbackend.caloriesummary.calculation.MealMacroTotals;
import com.example.caloriexbackend.common.enums.MealTime;
import com.example.caloriexbackend.entities.FoodLog;
import com.example.caloriexbackend.entities.User;
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
        double proteinGrams = (calories * user.getGoal().getProteinRatio()) / 4.0;
        double carbohydratesGrams = (calories * user.getGoal().getCarbsRatio()) / 4.0;
        double fatGrams = (calories * user.getGoal().getFatRatio()) / 9.0;

        return new DailyMacroTargets(proteinGrams, carbohydratesGrams, fatGrams);
    }

    private double calculateBmr(User user) {
        double weight = user.getActualWeightKg();
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        double base = (10 * weight) + (6.25 * user.getHeightCm()) - (5 * age);

        return base + user.getGender().getBmrOffset();
    }

    public MealMacroTotals calculateMealMacros(List<FoodLog> logs) {
        double calories = 0;
        double protein = 0;
        double carbs = 0;
        double fat = 0;

        for (FoodLog log : logs) {
            if (log.getCalories() != null) calories += log.getCalories();
            if (log.getProtein() != null) protein += log.getProtein();
            if (log.getCarbohydrates() != null) carbs += log.getCarbohydrates();
            if (log.getFat() != null) fat += log.getFat();
        }

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

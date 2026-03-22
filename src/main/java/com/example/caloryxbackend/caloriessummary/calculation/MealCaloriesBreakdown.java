package com.example.caloryxbackend.caloriessummary.calculation;

public record MealCaloriesBreakdown(
        double targetBreakfast,
        double targetLunch,
        double targetDinner,
        double targetSnack,

        double consumedBreakfast,
        double consumedLunch,
        double consumedDinner,
        double consumedSnack
) {}

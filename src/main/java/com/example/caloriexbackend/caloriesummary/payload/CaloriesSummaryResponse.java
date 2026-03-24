package com.example.caloriexbackend.caloriesummary.payload;

import java.time.LocalDate;

public record CaloriesSummaryResponse(
        LocalDate date,
        double targetCalories,
        double targetProteinGrams,
        double targetCarbohydratesGrams,
        double targetFatGrams,
        double consumedCalories,
        double consumedProteinGrams,
        double consumedCarbohydratesGrams,
        double consumedFatGrams,
        double targetBreakfastKcal,
        double consumedBreakfastKcal,
        double targetLunchKcal,
        double consumedLunchKcal,
        double targetDinnerKcal,
        double consumedDinnerKcal,
        double targetSnackKcal,
        double consumedSnackKcal
) {
}

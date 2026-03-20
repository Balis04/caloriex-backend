package com.example.caloryxbackend.caloriesummary.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        @JsonProperty("targetbreakfastkcal") double targetBreakfastKcal,
        @JsonProperty("consumedbreakfastkcal") double consumedBreakfastKcal,
        @JsonProperty("targetlunchkcal") double targetLunchKcal,
        @JsonProperty("consumedlunchkcal") double consumedLunchKcal,
        @JsonProperty("targetdinnerkcal") double targetDinnerKcal,
        @JsonProperty("consumeddinnerkcal") double consumedDinnerKcal,
        @JsonProperty("targetsnackkcal") double targetSnackKcal,
        @JsonProperty("consumedsnackkcal") double consumedSnackKcal
) {
}

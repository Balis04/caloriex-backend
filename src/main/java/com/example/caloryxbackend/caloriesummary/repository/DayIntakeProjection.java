package com.example.caloryxbackend.caloriesummary.repository;

public interface DayIntakeProjection {
    Double getCalories();
    Double getProtein();
    Double getCarbohydrates();
    Double getFat();
}

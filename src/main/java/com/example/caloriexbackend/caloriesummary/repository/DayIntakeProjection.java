package com.example.caloriexbackend.caloriesummary.repository;

public interface DayIntakeProjection {
    Double getCalories();
    Double getProtein();
    Double getCarbohydrates();
    Double getFat();
}

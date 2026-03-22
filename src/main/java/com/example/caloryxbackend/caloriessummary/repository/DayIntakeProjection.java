package com.example.caloryxbackend.caloriessummary.repository;

public interface DayIntakeProjection {
    Double getCalories();
    Double getProtein();
    Double getCarbohydrates();
    Double getFat();
}

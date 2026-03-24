package com.example.caloryxbackend.foodlog.payload.request;

import com.example.caloryxbackend.common.enums.MealTime;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FoodLogRequest {

    @NotBlank(message = "Food name is required")
    @Size(max = 100, message = "Food name is too long (max 100 characters)")
    private String foodName;

    @NotNull(message = "Meal time is required")
    private MealTime mealTime;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Calories required")
    @DecimalMin(value = "0.0", message = "Calories must be non-negative")
    private Double calories;

    @DecimalMin("0.0")
    private Double protein;

    @DecimalMin("0.0")
    private Double carbohydrates;

    @DecimalMin("0.0")
    private Double fat;

    private LocalDateTime consumedAt;
}

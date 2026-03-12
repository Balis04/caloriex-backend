package com.example.caloryxbackend.customfood.payload;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomFoodRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Calories are required")
    @DecimalMin(value = "0.0", message = "Calories cannot be negative")
    private Double calories;

    @NotNull(message = "Fat is required")
    @DecimalMin(value = "0.0", message = "Fat cannot be negative")
    private Double fat;

    @NotNull(message = "Carbohydrates are required")
    @DecimalMin(value = "0.0", message = "Carbohydrates cannot be negative")
    private Double carbohydrates;
}

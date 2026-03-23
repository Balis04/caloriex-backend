package com.example.caloryxbackend.usdafoods.payload;

import lombok.Data;

@Data
public class UsdaFoodNutrient {
    private int nutrientId;
    private String nutrientName;
    private Double value;
}
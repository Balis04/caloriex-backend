package com.example.caloriexbackend.usdafoods.payload;

import lombok.Data;

import java.util.List;

@Data
public class UsdaFood {
    private Integer fdcId;
    private String description;
    private String brandOwner;
    private Double servingSize;
    private String servingSizeUnit;

    private List<UsdaFoodNutrient> foodNutrients;
}
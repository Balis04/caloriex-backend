package com.example.caloriexbackend.usdafoods.payload;

import java.util.List;

public record UsdaFoodSearchResponse(
        List<UsdaFood> foods
) {}


package com.example.caloryxbackend.usdafoods.payload;

import java.util.List;

public record UsdaFoodSearchResponse(
        List<UsdaFood> foods
) {}


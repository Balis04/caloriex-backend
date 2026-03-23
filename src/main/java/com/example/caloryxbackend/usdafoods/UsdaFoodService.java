package com.example.caloryxbackend.usdafoods;

import com.example.caloryxbackend.usdafoods.payload.UsdaFood;
import com.example.caloryxbackend.usdafoods.payload.UsdaFoodItemResponse;
import com.example.caloryxbackend.usdafoods.payload.UsdaFoodNutrient;
import com.example.caloryxbackend.usdafoods.payload.UsdaFoodSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsdaFoodService {

    private final RestClient restClient;

    @Value("${usda.api-key}")
    private String apiKey;

    public List<UsdaFoodItemResponse> search(String query, String brand) {

        UsdaFoodSearchResponse response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .path("/fdc/v1/foods/search")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("dataType", "Branded")
                            .queryParam("pageSize", 20);

                    if (brand != null && !brand.isBlank()) {
                        uriBuilder.queryParam("brandOwner", brand.trim());
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(UsdaFoodSearchResponse.class);

        if (response == null || response.foods() == null) {
            throw new RuntimeException("USDA response invalid");
        }

        return response.foods().stream()
                .map(this::mapFood)
                .toList();
    }

    public UsdaFoodItemResponse mapFood(UsdaFood usdaFood) {
        List<UsdaFoodNutrient> nutrients = usdaFood.getFoodNutrients();

        return new UsdaFoodItemResponse(
                usdaFood.getFdcId(),
                usdaFood.getDescription(),
                usdaFood.getBrandOwner(),
                getNutrientValue(nutrients, 1008), // calories
                getNutrientValue(nutrients, 1003), // protein
                getNutrientValue(nutrients, 1005), // carbs
                getNutrientValue(nutrients, 1004), // fat
                usdaFood.getServingSize(),
                usdaFood.getServingSizeUnit()
        );
    }

    private Double getNutrientValue(List<UsdaFoodNutrient> nutrients, int nutrientId) {
        return nutrients.stream()
                .filter(n -> n.getNutrientId() == nutrientId)
                .map(UsdaFoodNutrient::getValue)
                .findFirst()
                .orElse(0.0);
    }
}


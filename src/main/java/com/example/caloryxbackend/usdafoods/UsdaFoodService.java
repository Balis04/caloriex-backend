package com.example.caloryxbackend.usdafoods;

import com.example.caloryxbackend.usdafoods.payload.UsdaFood;
import com.example.caloryxbackend.usdafoods.payload.UsdaFoodItemResponse;
import com.example.caloryxbackend.usdafoods.payload.UsdaFoodNutrient;
import com.example.caloryxbackend.usdafoods.payload.UsdaFoodSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsdaFoodService {

    private final RestClient restClient;

    @Value("${usda.api-key}")
    private String apiKey;

    public List<UsdaFoodItemResponse> search(String query, String brand) {
        try {
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
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "USDA response invalid");
            }

            return response.foods().stream()
                    .map(this::mapFood)
                    .toList();
        } catch (RestClientResponseException e) {

            if (e.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request to USDA");
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "USDA API error");

        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "USDA API unreachable or timed out");
        }
    }

    private  UsdaFoodItemResponse mapFood(UsdaFood usdaFood) {
        List<UsdaFoodNutrient> nutrients =
                usdaFood.getFoodNutrients() != null
                        ? usdaFood.getFoodNutrients()
                        : List.of();

        return new UsdaFoodItemResponse(
                usdaFood.getFdcId(),
                usdaFood.getDescription(),
                usdaFood.getBrandOwner(),
                getNutrientValue(nutrients, 1008), // calories
                getNutrientValue(nutrients, 1003), // protein
                getNutrientValue(nutrients, 1005), // carbohydrates
                getNutrientValue(nutrients, 1004), // fat
                usdaFood.getServingSize(),
                usdaFood.getServingSizeUnit()
        );
    }

    private Double getNutrientValue(List<UsdaFoodNutrient> nutrients, int nutrientId) {
        return nutrients.stream()
                .filter(n -> n.getNutrientId() == nutrientId && n.getValue() != null)
                .map(UsdaFoodNutrient::getValue)
                .findFirst()
                .orElse(0.0);
    }
}


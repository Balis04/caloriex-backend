package com.example.caloryxbackend.foods;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodProxyController {

    @Value("${USDA_API_KEY}")
    private String apiKey;

    private final RestClient restClient;

    @GetMapping("/search")
    public ResponseEntity<?> searchFood(
            @RequestParam String query,
            @RequestParam(required = false) String brand) {

        try {
            Object response = restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .scheme("https")
                                .host("api.nal.usda.gov")
                                .path("/fdc/v1/foods/search")
                                .queryParam("api_key", apiKey)
                                .queryParam("query", query)
                                .queryParam("dataType", "Branded");

                        if (brand != null && !brand.trim().isEmpty()) {
                            uriBuilder.queryParam("brandOwner", brand);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (request, responseEntity) -> {
                                throw new RuntimeException("USDA API hiba történt: " + responseEntity.getStatusCode());
                            })
                    .body(Object.class);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Hiba az USDA lekérés során: " + e.getMessage());
        }
    }
}
package com.example.caloryxbackend.foods;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("api/foods")
public class FoodProxyController {

    // A Spring beolvassa a környezeti változót (USDA_API_KEY)
    @Value("${USDA_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/search")
    public ResponseEntity<?> searchFood(
            @RequestParam String query,
            @RequestParam(required = false) String brand) {

        try {
            // Az USDA URL összeállítása
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://api.nal.usda.gov/fdc/v1/foods/search")
                    .queryParam("api_key", apiKey)
                    .queryParam("query", query)
                    .queryParam("brandOwner", brand)
                    .queryParam("dataType", "Branded")
                    .toUriString();

            // Kérés küldése az USDA-nak
            Object response = restTemplate.getForObject(url, Object.class);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Hiba az USDA lekérés során: " + e.getMessage());
        }
    }
}

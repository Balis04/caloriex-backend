package com.example.caloriexbackend.usdafoods.service;

import com.example.caloriexbackend.usdafoods.payload.UsdaFood;
import com.example.caloriexbackend.usdafoods.payload.UsdaFoodItemResponse;
import com.example.caloriexbackend.usdafoods.payload.UsdaFoodNutrient;
import com.example.caloriexbackend.usdafoods.payload.UsdaFoodSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsdaFoodServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private UsdaFoodService usdaFoodService;

    @BeforeEach
    void setUp() {
        usdaFoodService = new UsdaFoodService(restClient);
        ReflectionTestUtils.setField(usdaFoodService, "apiKey", "test-api-key");
    }

    @Test
    void searchSuccessfully() {
        UsdaFood food = new UsdaFood();
        food.setFdcId(123);
        food.setDescription("Protein Bar");
        food.setBrandOwner("BrandX");
        food.setServingSize(60.0);
        food.setServingSizeUnit("g");
        food.setFoodNutrients(List.of(
                nutrient(1008, 250.0),
                nutrient(1003, 20.0),
                nutrient(1005, 25.0),
                nutrient(1004, 8.0)
        ));
        UsdaFoodSearchResponse searchResponse = new UsdaFoodSearchResponse(List.of(food));

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UsdaFoodSearchResponse.class)).thenReturn(searchResponse);

        List<UsdaFoodItemResponse> actual = usdaFoodService.search("protein", "BrandX");

        assertEquals(1, actual.size());
        assertEquals(123, actual.getFirst().fdcId());
        assertEquals("Protein Bar", actual.getFirst().name());
        assertEquals("BrandX", actual.getFirst().brand());
        assertEquals(250.0, actual.getFirst().calories());
        assertEquals(20.0, actual.getFirst().protein());
        assertEquals(25.0, actual.getFirst().carbohydrates());
        assertEquals(8.0, actual.getFirst().fat());
        assertEquals(60.0, actual.getFirst().servingSize());
        assertEquals("g", actual.getFirst().servingUnit());
        verify(responseSpec).body(UsdaFoodSearchResponse.class);
    }

    @Test
    void searchZeroNutrients() {
        UsdaFood food = new UsdaFood();
        food.setFdcId(456);
        food.setDescription("Plain Water");
        food.setBrandOwner("BrandY");
        food.setServingSize(500.0);
        food.setServingSizeUnit("ml");
        food.setFoodNutrients(null);
        UsdaFoodSearchResponse searchResponse = new UsdaFoodSearchResponse(List.of(food));

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UsdaFoodSearchResponse.class)).thenReturn(searchResponse);

        List<UsdaFoodItemResponse> actual = usdaFoodService.search("water", null);

        assertEquals(0.0, actual.getFirst().calories());
        assertEquals(0.0, actual.getFirst().protein());
        assertEquals(0.0, actual.getFirst().carbohydrates());
        assertEquals(0.0, actual.getFirst().fat());
    }

    @Test
    void searchShouldResponseIsNull() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UsdaFoodSearchResponse.class)).thenReturn(null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usdaFoodService.search("protein", null)
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("USDA response invalid", exception.getReason());
    }

    @Test
    void searchBadRequest() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UsdaFoodSearchResponse.class)).thenThrow(
                new RestClientResponseException(
                        "Bad request",
                        400,
                        "Bad Request",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        StandardCharsets.UTF_8
                )
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usdaFoodService.search("protein", null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid request to USDA", exception.getReason());
    }

    @Test
    void searchBadGateway() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UsdaFoodSearchResponse.class)).thenThrow(
                new RestClientResponseException(
                        "Server error",
                        500,
                        "Internal Server Error",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        StandardCharsets.UTF_8
                )
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usdaFoodService.search("protein", null)
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("USDA API error", exception.getReason());
    }

    @Test
    void searchTimeout() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(UsdaFoodSearchResponse.class)).thenThrow(new ResourceAccessException("timeout"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usdaFoodService.search("protein", null)
        );

        assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatusCode());
        assertEquals("USDA API unreachable or timed out", exception.getReason());
    }

    private UsdaFoodNutrient nutrient(int nutrientId, Double value) {
        UsdaFoodNutrient nutrient = new UsdaFoodNutrient();
        nutrient.setNutrientId(nutrientId);
        nutrient.setValue(value);
        return nutrient;
    }
}



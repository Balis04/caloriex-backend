package com.example.caloryxbackend.usdafoods;

import com.example.caloryxbackend.usdafoods.payload.UsdaFoodItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@Validated
@Tag(name = "Foods", description = "USDA food search endpoints")
public class UsdaFoodController {

    private final UsdaFoodService usdaFoodService;

    @GetMapping("/search")
    @Operation(
            summary = "Search foods from USDA",
            description = "Search branded foods from USDA database by query and optional brand filter"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successful search",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UsdaFoodItemResponse.class)))
    )
    @ApiResponse(responseCode = "400", description = "Invalid input" , content = @Content)
    @ApiResponse(responseCode = "502", description = "External API error", content = @Content)
    public ResponseEntity<List<UsdaFoodItemResponse>> searchFood(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) String brand) {

        return ResponseEntity.ok(usdaFoodService.search(query, brand));
    }

}
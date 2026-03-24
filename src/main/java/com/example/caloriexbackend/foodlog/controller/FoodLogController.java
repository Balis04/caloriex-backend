package com.example.caloriexbackend.foodlog.controller;

import com.example.caloriexbackend.foodlog.service.FoodLogService;
import com.example.caloriexbackend.foodlog.payload.request.FoodLogAmountUpdateRequest;
import com.example.caloriexbackend.foodlog.payload.request.FoodLogRequest;
import com.example.caloriexbackend.foodlog.payload.response.FoodLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/food-log")
@RequiredArgsConstructor
@Tag(name = "Food Log", description = "Endpoints for managing food log entries")
public class FoodLogController {

    private final FoodLogService foodLogService;

    @PostMapping("")
    @Operation(
            summary = "Create food log",
            description = "Creates a new food log entry for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Food log created successfully",
                    content = @Content(schema = @Schema(implementation = FoodLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    public ResponseEntity<FoodLogResponse> createFoodLog(@Valid @RequestBody FoodLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(foodLogService.createFoodLog(request));
    }

    @PatchMapping("/{id}/amount")
    @Operation(
            summary = "Update food log amount",
            description = "Updates the amount of a food log entry and recalculates calories and macros proportionally."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Food log amount updated successfully",
                    content = @Content(schema = @Schema(implementation = FoodLogResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or original amount is missing or zero",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Food log not found",
                    content = @Content
            )
    })
    public ResponseEntity<FoodLogResponse> updateFoodLogAmount(
            @PathVariable UUID id,
            @Valid @RequestBody FoodLogAmountUpdateRequest request
    ) {
        return ResponseEntity.ok(foodLogService.updateFoodLogAmount(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete food log",
            description = "Deletes a food log entry for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Food log deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Food log not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteFoodLog(@PathVariable UUID id) {
        foodLogService.deleteFoodLog(id);
        return ResponseEntity.noContent().build();
    }
}

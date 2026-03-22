package com.example.caloryxbackend.customfood;

import com.example.caloryxbackend.common.exception.payload.ErrorResponse;
import com.example.caloryxbackend.customfood.payload.CustomFoodRequest;
import com.example.caloryxbackend.customfood.payload.CustomFoodResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/custom-foods")
@RequiredArgsConstructor
@Tag(name = "Custom Food Controller", description = "Endpoints for managing custom food items")
public class CustomFoodController {

    private final CustomFoodService customFoodService;

    @Operation(
            summary = "Create custom food",
            description = "Creates a new custom food item for the authenticated user. The food will be associated with the current user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Custom food created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomFoodResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (missing or invalid token)",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<CustomFoodResponse> create(@Valid @RequestBody CustomFoodRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customFoodService.create(request));
    }

    @Operation(
            summary = "Update custom food",
            description = "Updates an existing custom food item owned by the authenticated user. Only the owner can update the resource."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Custom food updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomFoodResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (missing or invalid token)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Custom food not found or not owned by user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomFoodResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CustomFoodRequest request
    ) {
        return ResponseEntity.ok(customFoodService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete custom food",
            description = "Deletes a custom food item owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Custom food deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized (missing or invalid token)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        customFoodService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all custom foods",
            description = "Retrieves all custom food items from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of custom foods retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomFoodResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<CustomFoodResponse>> getAll() {
        return ResponseEntity.ok(customFoodService.getAll());
    }

    @Operation(
            summary = "Get my custom foods",
            description = "Retrieves all custom food items created by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User's custom foods retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomFoodResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping("/mine")
    public ResponseEntity<List<CustomFoodResponse>> getMine() {
        return ResponseEntity.ok(customFoodService.getMine());
    }

    @Operation(
            summary = "Get other users' custom foods",
            description = "Retrieves all custom food items that are not owned by the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Other users' custom foods retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CustomFoodResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    @GetMapping("/not-mine")
    public ResponseEntity<List<CustomFoodResponse>> getNotMine() {
        return ResponseEntity.ok(customFoodService.getNotMine());
    }
}

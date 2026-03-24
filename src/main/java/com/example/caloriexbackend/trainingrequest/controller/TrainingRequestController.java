package com.example.caloriexbackend.trainingrequest.controller;

import com.example.caloriexbackend.trainingrequest.service.TrainingRequestService;
import com.example.caloriexbackend.trainingrequest.payload.request.TrainingRequestCreateRequest;
import com.example.caloriexbackend.trainingrequest.payload.response.TrainingRequestResponse;
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
@RequestMapping("/api/training-requests")
@RequiredArgsConstructor
@Tag(name = "Training Requests", description = "Endpoints for users to manage training requests")
public class TrainingRequestController {

    private final TrainingRequestService trainingRequestService;

    @GetMapping("/me")
    @Operation(
            summary = "Get my training requests",
            description = "Returns all training requests created by the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved training requests",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingRequestResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    public ResponseEntity<List<TrainingRequestResponse>> getMyRequests() {
        return ResponseEntity.ok(trainingRequestService.getMyRequests());
    }

    @PostMapping("/coach-profiles/{coachProfileId}")
    @Operation(
            summary = "Create training request",
            description = "Creates a new training request for the specified coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Training request created successfully",
                    content = @Content(schema = @Schema(implementation = TrainingRequestResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body </br>" +
                            "You cannot send a training request to your own coach profile",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Coach profile not found",
                    content = @Content
            )
    })
    public ResponseEntity<TrainingRequestResponse> create(
            @PathVariable UUID coachProfileId,
            @Valid @RequestBody TrainingRequestCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRequestService.create(coachProfileId, request));
    }
}

package com.example.caloryxbackend.trainingrequest.controller;

import com.example.caloryxbackend.common.enums.TrainingRequestStatus;
import com.example.caloryxbackend.trainingrequest.TrainingRequestService;
import com.example.caloryxbackend.trainingrequest.payload.request.TrainingRequestStatusUpdateRequest;
import com.example.caloryxbackend.trainingrequest.payload.response.ClosedTrainingRequestResponse;
import com.example.caloryxbackend.trainingrequest.trainingplan.payload.TrainingPlanResponse;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingRequestResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coach/training-requests")
@RequiredArgsConstructor
@Tag(name = "Coach Training Requests", description = "Endpoints for coaches to manage training requests")
public class TrainingRequestCoachController {

    private final TrainingRequestService trainingRequestService;

    @GetMapping()
    @Operation(
            summary = "Get training requests for my coach profile",
            description = "Returns training requests for the authenticated coach profile, optionally filtered by status."
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Coach profile not found",
                    content = @Content
            )
    })
    public ResponseEntity<List<TrainingRequestResponse>> getRequestsForMyCoachProfile(
            @RequestParam(required = false) TrainingRequestStatus status
    ) {
        return ResponseEntity.ok(trainingRequestService.getRequestsForMyCoachProfile(status));
    }

    @Operation(
            summary = "Get closed training requests for my coach profile",
            description = "Returns closed training requests for the authenticated coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved closed training requests",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ClosedTrainingRequestResponse.class)))
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
    @GetMapping("/closed")
    public ResponseEntity<List<ClosedTrainingRequestResponse>> getClosedRequestsForMyCoachProfile() {
        return ResponseEntity.ok(trainingRequestService.getClosedRequestsForMyCoachProfile());
    }

    @Operation(
            summary = "Update training request status",
            description = "Updates the status of a training request for the authenticated coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Training request status updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainingRequestResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or invalid status transition",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Training request or coach profile not found",
                    content = @Content
            )
    })
    @PatchMapping("/{trainingRequestId}/status")
    public ResponseEntity<TrainingRequestResponse> updateStatus(
            @PathVariable UUID trainingRequestId,
            @Valid @RequestBody TrainingRequestStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(trainingRequestService.updateStatus(trainingRequestId, request));
    }

    @Operation(
            summary = "Upload training plan",
            description = "Uploads a training plan file for a closed or approved training request of the authenticated coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Training plan uploaded successfully",
                    content = @Content(schema = @Schema(implementation = TrainingPlanResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid multipart request or unsupported file",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Training request not found",
                    content = @Content
            )
    })
    @PostMapping(value = "/{trainingRequestId}/training-plan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainingPlanResponse> uploadTrainingPlan(
            @PathVariable UUID trainingRequestId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "planName", required = false) String planName,
            @RequestParam(value = "description", required = false) String description
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRequestService.uploadTrainingPlan(trainingRequestId, file, planName, description));
    }
}

package com.example.caloryxbackend.trainingrequest.controller;

import com.example.caloryxbackend.common.enums.TrainingRequestStatus;
import com.example.caloryxbackend.trainingrequest.TrainingRequestService;
import com.example.caloryxbackend.trainingrequest.payload.request.TrainingRequestStatusUpdateRequest;
import com.example.caloryxbackend.trainingrequest.payload.response.ClosedTrainingRequestResponse;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingPlanResponse;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingRequestResponse;
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
public class TrainingRequestCoachController {

    private final TrainingRequestService trainingRequestService;

    @GetMapping()
    public ResponseEntity<List<TrainingRequestResponse>> getRequestsForMyCoachProfile(
            @RequestParam(required = false) TrainingRequestStatus status
    ) {
        return ResponseEntity.ok(trainingRequestService.getRequestsForMyCoachProfile(status));
    }

    @GetMapping("/closed")
    public ResponseEntity<List<ClosedTrainingRequestResponse>> getClosedRequestsForMyCoachProfile() {
        return ResponseEntity.ok(trainingRequestService.getClosedRequestsForMyCoachProfile());
    }

    @PatchMapping("/{trainingRequestId}/status")
    public ResponseEntity<TrainingRequestResponse> updateStatus(
            @PathVariable UUID trainingRequestId,
            @Valid @RequestBody TrainingRequestStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(trainingRequestService.updateStatus(trainingRequestId, request));
    }

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

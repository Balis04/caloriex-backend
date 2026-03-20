package com.example.caloryxbackend.trainingrequest.controller;

import com.example.caloryxbackend.trainingrequest.TrainingRequestService;
import com.example.caloryxbackend.trainingrequest.payload.request.TrainingRequestCreateRequest;
import com.example.caloryxbackend.trainingrequest.payload.response.TrainingRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TrainingRequestController {

    private final TrainingRequestService trainingRequestService;

    @GetMapping("/api/training-requests/me")
    public ResponseEntity<List<TrainingRequestResponse>> getMyRequests() {
        return ResponseEntity.ok(trainingRequestService.getMyRequests());
    }

    @PostMapping("/api/coach-profiles/{coachProfileId}/training-requests")
    public ResponseEntity<TrainingRequestResponse> create(
            @PathVariable UUID coachProfileId,
            @Valid @RequestBody TrainingRequestCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRequestService.create(coachProfileId, request));
    }
}

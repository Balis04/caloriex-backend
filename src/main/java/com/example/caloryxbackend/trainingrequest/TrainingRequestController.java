package com.example.caloryxbackend.trainingrequest;

import com.example.caloryxbackend.trainingrequest.payload.TrainingRequestCreateRequest;
import com.example.caloryxbackend.trainingrequest.payload.TrainingRequestResponse;
import com.example.caloryxbackend.trainingrequest.payload.TrainingRequestStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/api/coach-profiles/me/training-requests")
    public ResponseEntity<List<TrainingRequestResponse>> getRequestsForMyCoachProfile() {
        return ResponseEntity.ok(trainingRequestService.getRequestsForMyCoachProfile());
    }

    @PostMapping("/api/coach-profiles/{coachProfileId}/training-requests")
    public ResponseEntity<TrainingRequestResponse> create(
            @PathVariable UUID coachProfileId,
            @Valid @RequestBody TrainingRequestCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trainingRequestService.create(coachProfileId, request));
    }

    @PatchMapping("/api/training-requests/{trainingRequestId}/status")
    public ResponseEntity<TrainingRequestResponse> updateStatus(
            @PathVariable UUID trainingRequestId,
            @Valid @RequestBody TrainingRequestStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(trainingRequestService.updateStatus(trainingRequestId, request));
    }
}

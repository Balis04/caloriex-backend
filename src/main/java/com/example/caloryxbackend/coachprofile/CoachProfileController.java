package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.coachprofile.payload.CoachListResponse;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coach-profiles")
@RequiredArgsConstructor
public class CoachProfileController {

    private final CoachProfileService coachProfileService;

    @GetMapping
    public ResponseEntity<List<CoachListResponse>> getAll() {
        return ResponseEntity.ok(coachProfileService.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<CoachProfileResponse> getMine() {
        return ResponseEntity.ok(coachProfileService.getMine());
    }

    @PostMapping
    public ResponseEntity<CoachProfileResponse> create(@Valid @RequestBody CoachProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachProfileService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoachProfileResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CoachProfileRequest request
    ) {
        return ResponseEntity.ok(coachProfileService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        coachProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

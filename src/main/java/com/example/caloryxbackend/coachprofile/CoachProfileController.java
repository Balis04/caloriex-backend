package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloryxbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloryxbackend.coachprofile.payload.CoachListResponse;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/{id}/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CoachCertificateResponse> uploadCertificate(
            @PathVariable UUID id,
            @ModelAttribute CoachCertificateUploadRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coachProfileService.uploadCertificate(id, request));
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

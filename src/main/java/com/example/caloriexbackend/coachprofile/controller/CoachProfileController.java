package com.example.caloriexbackend.coachprofile.controller;

import com.example.caloriexbackend.coachprofile.CoachProfileService;
import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloriexbackend.coachprofile.payload.CoachListResponse;
import com.example.caloriexbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloriexbackend.coachprofile.payload.CoachProfileResponse;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coach-profiles")
@RequiredArgsConstructor
@Tag(name = "Coach Profiles", description = "Endpoints for managing coach profiles")
public class CoachProfileController {

    private final CoachProfileService coachProfileService;

    @GetMapping
    @Operation(
            summary = "Get all coach profiles",
            description = "Returns all available coach profiles."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved coach profiles",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CoachListResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    public ResponseEntity<List<CoachListResponse>> getAll() {
        return ResponseEntity.ok(coachProfileService.getAll());
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get my coach profile",
            description = "Returns the authenticated user's coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved coach profile",
                    content = @Content(schema = @Schema(implementation = CoachProfileResponse.class))
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
    public ResponseEntity<CoachProfileResponse> getMine() {
        return ResponseEntity.ok(coachProfileService.getMine());
    }

    @Operation(
            summary = "Create coach profile",
            description = "Creates a new coach profile for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Coach profile created successfully",
                    content = @Content(schema = @Schema(implementation = CoachProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body </br>" +
                            "Coach profile already exists for the current user",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<CoachProfileResponse> create(@Valid @RequestBody CoachProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachProfileService.create(request));
    }

    @Operation(
            summary = "Upload coach certificate",
            description = "Uploads a certificate for the specified coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Certificate uploaded successfully",
                    content = @Content(schema = @Schema(implementation = CoachCertificateResponse.class))
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
                    description = "Coach profile not found",
                    content = @Content
            )
    })
    @PostMapping(value = "/{id}/certificates", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CoachCertificateResponse> uploadCertificate(
            @PathVariable UUID id,
            @ModelAttribute CoachCertificateUploadRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(coachProfileService.uploadCertificate(id, request));
    }

    @Operation(
            summary = "Update coach profile",
            description = "Updates the specified coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Coach profile updated successfully",
                    content = @Content(schema = @Schema(implementation = CoachProfileResponse.class))
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - cannot update this coach profile",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Coach profile not found",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CoachProfileResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody CoachProfileRequest request
    ) {
        return ResponseEntity.ok(coachProfileService.update(id, request));
    }

    @Operation(
            summary = "Delete coach profile",
            description = "Deletes the specified coach profile."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Coach profile deleted successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - missing or invalid token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - cannot delete this coach profile",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Coach profile not found",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        coachProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.caloriexbackend.communitytrainingplan.controller;

import com.example.caloriexbackend.communitytrainingplan.payload.CommunityTrainingPlanResponse;
import com.example.caloriexbackend.communitytrainingplan.service.CommunityTrainingPlanService;
import com.example.caloriexbackend.storage.payload.StoredFileDownload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community-training-plans")
@RequiredArgsConstructor
@Tag(name = "Community Training Plans", description = "Public endpoints for pre-generated community training plans")
public class CommunityTrainingPlanController {

    private final CommunityTrainingPlanService communityTrainingPlanService;

    @GetMapping
    @Operation(
            summary = "List all community training plans",
            description = "Returns every pre-generated training plan found in the public community-training-plans storage folder."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved community training plans",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommunityTrainingPlanResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Storage is unavailable or not configured",
                    content = @Content
            )
    })
    public ResponseEntity<List<CommunityTrainingPlanResponse>> getAll() {
        return ResponseEntity.ok(communityTrainingPlanService.getAll());
    }

    @GetMapping("/{fileName:.+}/download")
    @Operation(
            summary = "Download a community training plan",
            description = "Downloads a pre-generated training plan from the public community-training-plans storage folder."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Training plan downloaded successfully",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file name or storage is unavailable",
                    content = @Content
            )
    })
    public ResponseEntity<ByteArrayResource> download(@PathVariable String fileName) {
        StoredFileDownload file = communityTrainingPlanService.download(fileName);

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (file.contentType() != null && !file.contentType().isBlank()) {
            mediaType = MediaType.parseMediaType(file.contentType());
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.content().length)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(file.fileName()).build().toString()
                )
                .body(new ByteArrayResource(file.content()));
    }
}

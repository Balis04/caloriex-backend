package com.example.caloriexbackend.communitytrainingplan.payload;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Publicly available community training plan")
public record CommunityTrainingPlanResponse(
        @Schema(example = "beginner-full-body.pdf")
        String fileName,
        @Schema(example = "245760")
        long size,
        @Schema(example = "2026-03-30T17:10:00Z")
        Instant lastModified,
        @Schema(example = "/api/community-training-plans/beginner-full-body.pdf/download")
        String downloadUrl
) {
}

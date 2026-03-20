package com.example.caloryxbackend.trainingrequest.payload.response;

import java.time.Instant;

public record TrainingPlanResponse(
        String planName,
        String description,
        String fileName,
        String fileUrl,
        Instant uploadedAt
) {
}

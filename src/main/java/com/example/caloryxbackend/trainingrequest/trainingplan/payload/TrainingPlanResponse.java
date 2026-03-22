package com.example.caloryxbackend.trainingrequest.trainingplan.payload;

import java.time.Instant;

public record TrainingPlanResponse(
        String planName,
        String description,
        String fileName,
        String fileUrl,
        Instant uploadedAt
) {
}

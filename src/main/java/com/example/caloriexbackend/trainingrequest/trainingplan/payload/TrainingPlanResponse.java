package com.example.caloriexbackend.trainingrequest.trainingplan.payload;

import java.time.Instant;

public record TrainingPlanResponse(
        String planName,
        String description,
        String fileName,
        Instant uploadedAt
) {
}

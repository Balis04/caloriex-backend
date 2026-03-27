package com.example.caloriexbackend.trainingrequest.trainingplan.payload;

import java.time.Instant;

public record TrainingPlanResponse(
        String planName,
        String planDescription,
        String fileName,
        Instant uploadedAt
) {
}

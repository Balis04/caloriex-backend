package com.example.caloriexbackend.trainingrequest.payload.response;

import java.time.Instant;
import java.util.UUID;

public record ClosedTrainingRequestResponse(
        UUID requestId,
        String coachName,
        String requesterName,
        String requesterEmail,
        Integer weeklyTrainingCount,
        Integer sessionDurationMinutes,
        String preferredLocation,
        String status,
        String description,
        String coachNote,
        Instant createdAt,

        // training plan
        String planName,
        String planDescription,
        String fileName,
        String fileUrl,
        Instant uploadedAt
) {
}

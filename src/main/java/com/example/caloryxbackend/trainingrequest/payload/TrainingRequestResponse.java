package com.example.caloryxbackend.trainingrequest.payload;

import com.example.caloryxbackend.trainingrequest.model.TrainingRequestStatus;

import java.time.Instant;
import java.util.UUID;

public record TrainingRequestResponse(
        UUID id,
        UUID coachProfileId,
        UUID requesterUserId,
        String coachName,
        String requesterName,
        String requesterEmail,
        Integer weeklyTrainingCount,
        Integer sessionDurationMinutes,
        String preferredLocation,
        TrainingRequestStatus status,
        String coachNote,
        Instant createdAt
) {
}

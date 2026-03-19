package com.example.caloryxbackend.trainingrequest.payload.response;

import com.example.caloryxbackend.common.enums.TrainingRequestStatus;

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
        String description,
        String coachNote,
        Instant createdAt
) {
}

package com.example.caloryxbackend.coachprofile.payload;

import com.example.caloryxbackend.coachprofile.model.Currency;
import com.example.caloryxbackend.coachprofile.model.TrainingFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CoachListResponse(
        UUID id,
        UUID userId,
        String trainerName,
        String email,
        LocalDate trainingStartedAt,
        String shortDescription,
        TrainingFormat trainingFormat,
        Integer priceFrom,
        Integer priceTo,
        Currency currency,
        Integer maxCapacity,
        String contactNote,
        List<CoachAvailabilityResponse> availabilities,
        List<CoachCertificateResponse> certificates,
        Instant createdAt,
        Instant updatedAt
) {
}

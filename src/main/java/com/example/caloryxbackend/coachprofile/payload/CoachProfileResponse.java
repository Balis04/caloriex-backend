package com.example.caloryxbackend.coachprofile.payload;

import com.example.caloryxbackend.coachprofile.coachavailability.payload.CoachAvailabilityResponse;
import com.example.caloryxbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloryxbackend.common.enums.Currency;
import com.example.caloryxbackend.common.enums.TrainingFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CoachProfileResponse(
        UUID id,
        UUID userId,
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

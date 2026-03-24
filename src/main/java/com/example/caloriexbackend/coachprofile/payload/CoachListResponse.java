package com.example.caloriexbackend.coachprofile.payload;

import com.example.caloriexbackend.coachprofile.coachavailability.payload.CoachAvailabilityResponse;
import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloriexbackend.common.enums.Currency;
import com.example.caloriexbackend.common.enums.TrainingFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CoachListResponse(
        UUID id,
        UUID userId,
        String coachName,
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

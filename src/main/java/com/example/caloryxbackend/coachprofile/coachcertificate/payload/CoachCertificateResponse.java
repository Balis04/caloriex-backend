package com.example.caloryxbackend.coachprofile.coachcertificate.payload;

import java.time.Instant;
import java.util.UUID;

public record CoachCertificateResponse(
        UUID id,
        String certificateName,
        String issuer,
        Instant issuedAt,
        String fileUrl,
        Instant uploadedAt
) {}

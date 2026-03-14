package com.example.caloryxbackend.coachprofile.payload;

import java.time.Instant;
import java.util.UUID;

public record CoachCertificateResponse(
        UUID id,
        String fileName,
        String certificateName,
        String issuer,
        Instant issuedAt,
        String fileUrl,
        String contentType,
        Long fileSizeBytes,
        Instant uploadedAt
) {
}

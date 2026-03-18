package com.example.caloryxbackend.coachprofile.payload;

public record CoachCertificateUploadResponse(
        String fileName,
        String fileUrl,
        String contentType,
        Long fileSizeBytes
) {
}

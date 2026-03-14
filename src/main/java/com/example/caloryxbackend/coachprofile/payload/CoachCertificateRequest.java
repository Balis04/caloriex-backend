package com.example.caloryxbackend.coachprofile.payload;

import lombok.Data;

import java.time.Instant;

@Data
public class CoachCertificateRequest {

    private String fileName;

    private String certificateName;

    private String issuer;

    private Instant issuedAt;

    private String fileUrl;

    private String contentType;

    private Long fileSizeBytes;
}

package com.example.caloryxbackend.coachprofile.coachcertificate.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Data
public class CoachCertificateUploadRequest {

    @Schema(type = "string", format = "binary")
    private MultipartFile file;

    private String certificateName;

    private String issuer;

    private Instant issuedAt;
}

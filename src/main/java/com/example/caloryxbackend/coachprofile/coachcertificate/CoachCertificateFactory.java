package com.example.caloryxbackend.coachprofile.coachcertificate;

import com.example.caloryxbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloryxbackend.entities.CoachCertificate;
import com.example.caloryxbackend.entities.CoachProfile;
import com.example.caloryxbackend.storage.payload.DocumentUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoachCertificateFactory {

    private final CoachCertificateMapper mapper;

    public CoachCertificate create(
            CoachCertificateUploadRequest request,
            CoachProfile profile,
            DocumentUploadResponse upload
    ) {
        CoachCertificate entity = mapper.toEntity(request);

        entity.setCoachProfile(profile);
        entity.setFileName(upload.originalFileName());
        entity.setFileUrl(upload.fileUrl());
        entity.setContentType(upload.contentType());
        entity.setFileSizeBytes(upload.size());

        return entity;
    }
}

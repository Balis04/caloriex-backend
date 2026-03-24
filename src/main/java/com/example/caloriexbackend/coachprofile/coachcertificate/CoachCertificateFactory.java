package com.example.caloriexbackend.coachprofile.coachcertificate;

import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloriexbackend.entities.CoachCertificate;
import com.example.caloriexbackend.entities.CoachProfile;
import com.example.caloriexbackend.storage.payload.DocumentUploadResponse;
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

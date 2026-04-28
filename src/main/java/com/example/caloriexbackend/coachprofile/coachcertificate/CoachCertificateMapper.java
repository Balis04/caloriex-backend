package com.example.caloriexbackend.coachprofile.coachcertificate;

import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateResponse;
import com.example.caloriexbackend.coachprofile.coachcertificate.payload.CoachCertificateUploadRequest;
import com.example.caloriexbackend.entities.CoachCertificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoachCertificateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coachProfile", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "fileUrl", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "fileSizeBytes", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CoachCertificate toEntity(CoachCertificateUploadRequest request);

    CoachCertificateResponse toResponse(CoachCertificate entity);

    default List<CoachCertificateResponse> toResponseList(List<CoachCertificate> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
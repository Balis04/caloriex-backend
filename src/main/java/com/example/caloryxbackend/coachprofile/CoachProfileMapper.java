package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.coachprofile.coachavailability.CoachAvailabilityMapper;
import com.example.caloryxbackend.coachprofile.coachcertificate.CoachCertificateMapper;
import com.example.caloryxbackend.coachprofile.payload.CoachListResponse;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloryxbackend.coachprofile.payload.CoachProfileResponse;
import com.example.caloryxbackend.entities.CoachProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        uses = {
                CoachAvailabilityMapper.class,
                CoachCertificateMapper.class
        }
)
public interface CoachProfileMapper {

    @Mapping(target = "userId", source = "user.id")
    CoachProfileResponse toResponse(CoachProfile entity);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "trainerName", source = "user.fullName")
    @Mapping(target = "email", source = "user.email")
    CoachListResponse toListResponse(CoachProfile entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "availabilities", ignore = true)
    @Mapping(target = "certificates", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFromRequest(
            CoachProfileRequest request,
            @MappingTarget CoachProfile entity
    );
}
package com.example.caloriexbackend.coachprofile.mapper;

import com.example.caloriexbackend.coachprofile.coachavailability.CoachAvailabilityMapper;
import com.example.caloriexbackend.coachprofile.coachcertificate.CoachCertificateMapper;
import com.example.caloriexbackend.coachprofile.payload.CoachListResponse;
import com.example.caloriexbackend.coachprofile.payload.CoachProfileRequest;
import com.example.caloriexbackend.coachprofile.payload.CoachProfileResponse;
import com.example.caloriexbackend.entities.CoachProfile;
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
    @Mapping(target = "coachName", source = "user.fullName")
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

package com.example.caloriexbackend.coachprofile.coachavailability;

import com.example.caloriexbackend.coachprofile.coachavailability.payload.CoachAvailabilityRequest;
import com.example.caloriexbackend.coachprofile.coachavailability.payload.CoachAvailabilityResponse;
import com.example.caloriexbackend.entities.CoachAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CoachAvailabilityMapper {

    CoachAvailabilityResponse toResponse(CoachAvailability entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coachProfile", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CoachAvailability toEntity(CoachAvailabilityRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    default List<CoachAvailabilityResponse> toResponseList(List<CoachAvailability> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(CoachAvailability::getDayOfWeek))
                .map(this::toResponse)
                .toList();
    }
}

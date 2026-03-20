package com.example.caloryxbackend.coachprofile.coachavailability;

import com.example.caloryxbackend.coachprofile.coachavailability.payload.CoachAvailabilityRequest;
import com.example.caloryxbackend.coachprofile.coachavailability.payload.CoachAvailabilityResponse;
import com.example.caloryxbackend.entities.CoachAvailability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CoachAvailabilityMapper {

    CoachAvailabilityResponse toResponse(CoachAvailability entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coachProfile", ignore = true)
    CoachAvailability toEntity(CoachAvailabilityRequest request);

    default List<CoachAvailabilityResponse> toResponseList(List<CoachAvailability> entities) {
        return entities.stream()
                .sorted(Comparator.comparing(CoachAvailability::getDayOfWeek))
                .map(this::toResponse)
                .toList();
    }
}

package com.example.caloryxbackend.coachprofile.coachavailability;

import com.example.caloryxbackend.coachprofile.coachavailability.payload.CoachAvailabilityRequest;
import com.example.caloryxbackend.entities.CoachAvailability;
import com.example.caloryxbackend.entities.CoachProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CoachAvailabilityFactory {

    private final CoachAvailabilityMapper mapper;

    public List<CoachAvailability> createList(
            CoachProfile profile,
            List<CoachAvailabilityRequest> requests
    ) {
        return requests.stream()
                .filter(r -> Boolean.TRUE.equals(r.getAvailable()))
                .map(r -> {
                    CoachAvailability entity = mapper.toEntity(r);
                    entity.setCoachProfile(profile);
                    entity.setAvailable(true);
                    return entity;
                })
                .toList();
    }
}
package com.example.caloriexbackend.coachprofile.coachavailability;

import com.example.caloriexbackend.coachprofile.coachavailability.payload.CoachAvailabilityRequest;
import com.example.caloriexbackend.entities.CoachAvailability;
import com.example.caloriexbackend.entities.CoachProfile;
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
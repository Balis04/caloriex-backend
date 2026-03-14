package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.entities.CoachAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoachAvailabilityRepository extends JpaRepository<CoachAvailability, UUID> {

    void deleteAllByCoachProfileId(UUID coachProfileId);
}

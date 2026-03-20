package com.example.caloryxbackend.coachprofile.coachavailability;

import com.example.caloryxbackend.entities.CoachAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface CoachAvailabilityRepository extends JpaRepository<CoachAvailability, UUID> {

    @Modifying
    @Query("DELETE FROM CoachAvailability a WHERE a.coachProfile.id = :id")
    void deleteAllByCoachProfileId(@Param("id") UUID id);
}

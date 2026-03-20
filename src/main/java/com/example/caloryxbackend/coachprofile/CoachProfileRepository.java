package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.entities.CoachProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, UUID> {

    boolean existsByUserId(UUID userId);

    Optional<CoachProfile> findByIdAndUserId(UUID id, UUID userId);

    Optional<CoachProfile> findByUserId(UUID userId);

    List<CoachProfile> findAllByUserIdNot(UUID userId);
}

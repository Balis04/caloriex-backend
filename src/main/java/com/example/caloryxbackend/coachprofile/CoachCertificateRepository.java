package com.example.caloryxbackend.coachprofile;

import com.example.caloryxbackend.entities.CoachCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoachCertificateRepository extends JpaRepository<CoachCertificate, UUID> {

    void deleteAllByCoachProfileId(UUID coachProfileId);
}

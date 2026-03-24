package com.example.caloriexbackend.coachprofile.coachcertificate;

import com.example.caloriexbackend.entities.CoachCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoachCertificateRepository extends JpaRepository<CoachCertificate, UUID> {

    void deleteAllByCoachProfileId(UUID coachProfileId);
}

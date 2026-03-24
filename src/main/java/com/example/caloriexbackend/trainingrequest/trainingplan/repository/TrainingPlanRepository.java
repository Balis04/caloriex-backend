package com.example.caloriexbackend.trainingrequest.trainingplan.repository;

import com.example.caloriexbackend.entities.TrainingPlan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, UUID> {

    boolean existsByTrainingRequestId(UUID trainingRequestId);

    Optional<TrainingPlan> findByTrainingRequestId(UUID trainingRequestId);

    @EntityGraph(attributePaths = {
            "trainingRequest",
            "trainingRequest.coachProfile",
            "trainingRequest.coachProfile.user",
            "trainingRequest.requesterUser"
    })
    List<TrainingPlan> findAllByCoachUserIdOrderByUploadedAtDesc(UUID coachUserId);
}

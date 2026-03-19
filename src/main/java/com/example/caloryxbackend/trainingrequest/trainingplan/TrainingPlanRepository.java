package com.example.caloryxbackend.trainingrequest.trainingplan;

import com.example.caloryxbackend.entities.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, UUID> {

    boolean existsByTrainingRequestId(UUID trainingRequestId);

    Optional<TrainingPlan> findByTrainingRequestId(UUID trainingRequestId);

    List<TrainingPlan> findAllByCoachUserIdOrderByUploadedAtDesc(UUID coachUserId);
}

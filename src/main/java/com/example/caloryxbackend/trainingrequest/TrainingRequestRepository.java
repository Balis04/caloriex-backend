package com.example.caloryxbackend.trainingrequest;

import com.example.caloryxbackend.entities.TrainingRequest;
import com.example.caloryxbackend.common.enums.TrainingRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainingRequestRepository extends JpaRepository<TrainingRequest, UUID> {

    List<TrainingRequest> findAllByRequesterUserIdOrderByCreatedAtDesc(UUID requesterUserId);

    List<TrainingRequest> findAllByCoachProfileUserIdOrderByCreatedAtDesc(UUID coachUserId);

    List<TrainingRequest> findAllByCoachProfileUserIdAndStatusOrderByCreatedAtDesc(UUID coachUserId, TrainingRequestStatus status);

    Optional<TrainingRequest> findByIdAndCoachProfileUserId(UUID id, UUID coachUserId);
}

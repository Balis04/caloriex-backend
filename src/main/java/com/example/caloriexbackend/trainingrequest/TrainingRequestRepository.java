package com.example.caloriexbackend.trainingrequest;

import com.example.caloriexbackend.common.enums.TrainingRequestStatus;
import com.example.caloriexbackend.entities.TrainingRequest;
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

package com.example.caloriexbackend.foodlog;

import com.example.caloriexbackend.entities.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FoodLogRepository extends JpaRepository<FoodLog, UUID> {

    Optional<FoodLog> findByIdAndUserId(UUID id, UUID userId);
}

package com.example.caloryxbackend.foodlog;

import com.example.caloryxbackend.entities.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FoodLogRepository extends JpaRepository<FoodLog, UUID> {

    Optional<FoodLog> findByIdAndAuth0Id(UUID id, String auth0Id);
}

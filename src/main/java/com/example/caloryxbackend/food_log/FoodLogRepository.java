package com.example.caloryxbackend.food_log;

import com.example.caloryxbackend.entities.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FoodLogRepository extends JpaRepository<FoodLog, UUID> {
}

package com.example.caloryxbackend.caloriessummary.repository;

import com.example.caloryxbackend.entities.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CaloriesSummaryRepository extends JpaRepository<FoodLog, UUID> {

    @Query("""
            SELECT
                COALESCE(SUM(fl.calories), 0) AS calories,
                COALESCE(SUM(fl.protein), 0) AS protein,
                COALESCE(SUM(fl.carbohydrates), 0) AS carbohydrates,
                COALESCE(SUM(fl.fat), 0) AS fat
            FROM FoodLog fl
            WHERE fl.auth0Id = :auth0Id
              AND fl.consumedAt >= :start
              AND fl.consumedAt < :end
            """)
    DayIntakeProjection findTodayIntake(
            @Param("auth0Id") String auth0Id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<FoodLog> findByAuth0IdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
            String auth0Id,
            LocalDateTime start,
            LocalDateTime end
    );
}

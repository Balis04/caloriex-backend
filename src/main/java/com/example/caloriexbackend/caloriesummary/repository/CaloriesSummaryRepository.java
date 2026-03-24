package com.example.caloriexbackend.caloriesummary.repository;

import com.example.caloriexbackend.common.enums.MealTime;
import com.example.caloriexbackend.entities.FoodLog;
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
            WHERE fl.user.id = :userId
              AND fl.consumedAt >= :start
              AND fl.consumedAt < :end
            """)
    DayIntakeProjection findTodayIntake(
            @Param("userId") UUID userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<FoodLog> findByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<FoodLog> findByUserIdAndMealTimeAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtDesc(
            UUID userId,
            MealTime mealTime,
            LocalDateTime start,
            LocalDateTime end
    );
}

package com.example.caloryxbackend.user;

import com.example.caloryxbackend.user.enums.ActivityLevel;
import com.example.caloryxbackend.user.enums.GoalType;
import com.example.caloryxbackend.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "auth0_id", nullable = false, unique = true)
    private String auth0id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal")
    private GoalType goal;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;

    @Column(name = "start_weight_kg")
    private Double startWeightKg;

    @Column(name = "target_weight_kg")
    private Double targetWeightKg;

    @Column(name = "weekly_goal_kg")
    private Double weeklyGoalKg;

    @Column(name = "actual_weight_kg")
    private Double actualWeightKg;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "profile_completed", nullable = false)
    private boolean profileCompleted;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (role == null) {
            role = UserRole.USER; // default fallback
        }
    }
}

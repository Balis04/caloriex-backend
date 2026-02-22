package com.example.caloryxbackend.entities;

import com.example.caloryxbackend.user.model.enums.ActivityLevel;
import com.example.caloryxbackend.user.model.enums.Gender;
import com.example.caloryxbackend.user.model.enums.GoalType;
import com.example.caloryxbackend.user.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
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

    @Column(name = "full_name")
    private String fullName;

    @Column(name ="birth_date")
    private Date birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "start_weight_kg")
    private Double startWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level")
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal")
    private GoalType goal;

    @Column(name = "target_weight_kg")
    private Double targetWeightKg;

    @Column(name = "weekly_goal_kg")
    private Double weeklyGoalKg;

    @Column(name = "actual_weight_kg")
    private Double actualWeightKg;

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

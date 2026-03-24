package com.example.caloriexbackend.entities;

import com.example.caloriexbackend.common.enums.ActivityLevel;
import com.example.caloriexbackend.common.enums.Gender;
import com.example.caloriexbackend.common.enums.GoalType;
import com.example.caloriexbackend.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
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

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name ="birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "height_cm", nullable = false)
    private Integer heightCm;

    @Column(name = "start_weight_kg", nullable = false)
    private Double startWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal", nullable = false)
    private GoalType goal;

    @Column(name = "target_weight_kg", nullable = false)
    private Double targetWeightKg;

    @Column(name = "weekly_goal_kg", nullable = false)
    private Double weeklyGoalKg;

    @Column(name = "actual_weight_kg", nullable = false)
    private Double actualWeightKg;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "user")
    private CoachProfile coachProfile;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }

        if (role == null) {
            role = UserRole.USER;
        }
    }
}

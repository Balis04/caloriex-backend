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
    private UUID id;

    private String name;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private GoalType goal;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    private Double startWeightKg;

    private Double targetWeightKg;

    private Double weeklyGoalKg;

    private Double actualWeightKg;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}

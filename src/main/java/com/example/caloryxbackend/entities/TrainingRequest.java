package com.example.caloryxbackend.entities;

import com.example.caloryxbackend.common.enums.TrainingRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "training_requests")
@Getter
@Setter
public class TrainingRequest {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requesterUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_profile_id", nullable = false)
    private CoachProfile coachProfile;

    @Column(name = "weekly_training_count", nullable = false)
    private Integer weeklyTrainingCount;

    @Column(name = "session_duration_minutes", nullable = false)
    private Integer sessionDurationMinutes;

    @Column(name = "preferred_location", nullable = false, length = 255)
    private String preferredLocation;

    @Column(name = "coach_note", columnDefinition = "text")
    private String coachNote;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TrainingRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(mappedBy = "trainingRequest")
    private TrainingPlan trainingPlan;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (status == null) {
            status = TrainingRequestStatus.PENDING;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

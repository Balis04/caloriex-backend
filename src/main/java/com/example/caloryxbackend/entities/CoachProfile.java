package com.example.caloryxbackend.entities;

import com.example.caloryxbackend.common.enums.Currency;
import com.example.caloryxbackend.common.enums.TrainingFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "coach_profiles")
@Getter
@Setter
public class CoachProfile {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "training_started_at")
    private LocalDate trainingStartedAt;

    @Column(name = "short_description", columnDefinition = "text")
    private String shortDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_format", length = 20)
    private TrainingFormat trainingFormat;

    @Column(name = "price_from")
    private Integer priceFrom;

    @Column(name = "price_to")
    private Integer priceTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 10)
    private Currency currency;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "contact_note", columnDefinition = "text")
    private String contactNote;

    @OneToMany(
            mappedBy = "coachProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @org.hibernate.annotations.BatchSize(size = 20)
    private List<CoachAvailability> availabilities = new ArrayList<>();

    @OneToMany(
            mappedBy = "coachProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @org.hibernate.annotations.BatchSize(size = 20)
    private List<CoachCertificate> certificates = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

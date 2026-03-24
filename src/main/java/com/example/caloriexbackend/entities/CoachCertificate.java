package com.example.caloriexbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coach_certificates")
@Getter
@Setter
public class CoachCertificate {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_profile_id", nullable = false)
    private CoachProfile coachProfile;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "certificate_name", length = 255)
    private String certificateName;

    @Column(name = "issuer", length = 255)
    private String issuer;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

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
        if (uploadedAt == null) {
            uploadedAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}

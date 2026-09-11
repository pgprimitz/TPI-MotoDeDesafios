package com.tpi.challengeengine.entities;

import com.tpi.challengeengine.enums.ChallengeDifficulty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representacion JPA de una version historica en la tabla
 * {@code challenge_version}. Conserva el vinculo mediante
 * {@code challengeId}; la integridad de la relacion se delega a PostgreSQL y
 * no se modela como una asociacion ORM.
 */
@Entity
@Table(
        name = "challenge_version",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_challenge_version_number",
                columnNames = {"challenge_id", "version_number"}
        )
)
public class ChallengeVersionEntity {

    @Id
    private UUID id;

    @Column(name = "challenge_id", nullable = false)
    private UUID challengeId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ChallengeDifficulty difficulty;

    @Column(name = "content_reference", length = 500)
    private String contentReference;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected ChallengeVersionEntity() {
    }

    public ChallengeVersionEntity(
            UUID id,
            UUID challengeId,
            Integer versionNumber,
            String title,
            String description,
            ChallengeDifficulty difficulty,
            String contentReference,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.challengeId = challengeId;
        this.versionNumber = versionNumber;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.contentReference = contentReference;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getChallengeId() {
        return challengeId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ChallengeDifficulty getDifficulty() {
        return difficulty;
    }

    public String getContentReference() {
        return contentReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

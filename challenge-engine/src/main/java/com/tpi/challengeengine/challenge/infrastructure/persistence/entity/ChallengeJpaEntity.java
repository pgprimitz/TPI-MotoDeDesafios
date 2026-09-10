package com.tpi.challengeengine.challenge.infrastructure.persistence.entity;

import com.tpi.challengeengine.challenge.domain.enums.ChallengeStatus;
import com.tpi.challengeengine.challenge.domain.enums.ChallengeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representacion JPA de la identidad estable de un desafio en la tabla
 * {@code challenge}. No contiene comportamiento ni relaciones con entidades
 * de otros dominios.
 */
@Entity
@Table(name = "challenge")
public class ChallengeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "author_id")
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ChallengeType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ChallengeStatus status;

    @Column(name = "current_version")
    private Integer currentVersion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    protected ChallengeJpaEntity() {
    }

    public ChallengeJpaEntity(
            UUID id,
            UUID authorId,
            ChallengeType type,
            ChallengeStatus status,
            Integer currentVersion,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime archivedAt
    ) {
        this.id = id;
        this.authorId = authorId;
        this.type = type;
        this.status = status;
        this.currentVersion = currentVersion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.archivedAt = archivedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public ChallengeType getType() {
        return type;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
}

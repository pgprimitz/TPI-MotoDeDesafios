package com.tpi.challengeengine.model;

import com.tpi.challengeengine.enums.ChallengeStatus;
import com.tpi.challengeengine.enums.ChallengeType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un desafio administrado por el Motor de Desafios. Mantiene la
 * identidad general y el estado del desafio. El contenido especifico teorico
 * o practico pertenece a otros servicios. Sus versiones historicas se
 * representan mediante {@link ChallengeVersion}.
 */
public class Challenge {

    private UUID id;
    private UUID authorId;
    private ChallengeType type;
    private ChallengeStatus status;
    private Integer currentVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;

    public Challenge(
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

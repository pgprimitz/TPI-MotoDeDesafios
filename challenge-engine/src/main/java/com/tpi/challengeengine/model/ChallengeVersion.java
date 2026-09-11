package com.tpi.challengeengine.model;

import com.tpi.challengeengine.enums.ChallengeDifficulty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa una version historica de un {@link Challenge}.
 * Cada instancia conserva un estado historico del desafio y los futuros
 * intentos referenciaran una version especifica. {@code contentReference}
 * apunta al contenido externo: T03 no almacena preguntas, codigo ni tests.
 */
public class ChallengeVersion {

    private UUID id;
    private UUID challengeId;
    private Integer versionNumber;
    private String title;
    private String description;
    private ChallengeDifficulty difficulty;
    private String contentReference;
    private LocalDateTime createdAt;

    public ChallengeVersion(
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

package com.tpi.challengeengine.challenge.domain.model;

import com.tpi.challengeengine.challenge.domain.enums.ChallengeStatus;
import com.tpi.challengeengine.challenge.domain.enums.ChallengeType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un desafio administrado por el Motor de Desafios.
 * Mantiene la identidad general del desafio y su estado de ciclo de vida.
 * El contenido especifico teorico o practico no pertenece a esta clase,
 * ya que sera administrado por otros microservicios. Sus versiones historicas
 * se representaran mediante {@link ChallengeVersion}.
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
}

package com.tpi.challengeengine.challenge.domain.model;

import com.tpi.challengeengine.challenge.domain.enums.ChallengeDifficulty;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa una version historica de un {@link Challenge}.
 * Cada edicion relevante del desafio podra generar una nueva version. Los
 * intentos del alumno quedaran vinculados a una version especifica para
 * preservar exactamente que desafio resolvio. El contenido especializado no
 * se almacena aqui: solo se conserva una referencia administrada por T04 o T05.
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
}

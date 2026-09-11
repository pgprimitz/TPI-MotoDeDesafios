package com.tpi.challengeengine.enums;

/**
 * Representa el estado de ciclo de vida de un desafio, sin definir sus
 * transiciones.
 */
public enum ChallengeStatus {
    /** Desafio todavia no publicado. */
    DRAFT,

    /** Desafio disponible en el catalogo conforme a las futuras reglas. */
    PUBLISHED,

    /** Desafio dado de baja logicamente, conservando su historial. */
    ARCHIVED
}

package com.tpi.challengeengine.outbox.domain.enums;

/**
 * Representa el estado tecnico de publicacion de un evento Outbox, sin
 * implementar reglas de reintento.
 */
public enum OutboxEventStatus {
    /** Evento aun no publicado. */
    PENDING,

    /** Evento publicado correctamente. */
    PUBLISHED,

    /** El ultimo intento de publicacion fallo. */
    FAILED
}

package com.tpi.challengeengine.enums;

/**
 * Representa el estado tecnico del ciclo de vida de un intento.
 *
 * <p>{@link #FAILED} se reservara conceptualmente para errores tecnicos y no
 * debe interpretarse automaticamente como desaprobacion academica. Este enum
 * no define transiciones entre estados.</p>
 */
public enum AttemptStatus {
    STARTED,
    SUBMITTED,
    EVALUATING,
    COMPLETED,
    FAILED
}

package com.tpi.challengeengine.enums;

/**
 * Representa el estado academico comun del resultado de un intento.
 * Este estado es diferente de {@code AttemptStatus}, que describe el estado
 * tecnico del intento, y no incorpora reglas de evaluacion.
 */
public enum AcademicResultStatus {
    PASSED,
    FAILED,
    PENDING
}

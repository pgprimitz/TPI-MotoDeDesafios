package com.tpi.challengeengine.result.domain.model;

import com.tpi.challengeengine.result.domain.enums.AcademicResultStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa el resultado academico comun de un intento.
 * Normaliza el resultado producido por los servicios especializados T04 o T05
 * y se relaciona con el intento mediante su identificador. No contiene
 * preguntas, codigo, tests ni feedback detallado.
 */
public class AttemptResult {

    private UUID id;
    private UUID attemptId;
    private AcademicResultStatus status;
    private BigDecimal score;
    private String evaluationReference;
    private LocalDateTime evaluatedAt;
}

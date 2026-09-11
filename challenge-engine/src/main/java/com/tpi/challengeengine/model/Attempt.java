package com.tpi.challengeengine.model;

import com.tpi.challengeengine.enums.AttemptStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un intento de un alumno sobre una version especifica de un
 * desafio.
 *
 * <p>El intento conserva mediante identificadores la referencia historica de
 * la version utilizada y el contexto academico externo en el que fue
 * realizado. Esta clase solamente define la estructura inicial; las reglas de
 * elegibilidad, reintentos y transiciones de estado se implementaran
 * posteriormente.</p>
 */
public class Attempt {

    private UUID id;
    private UUID studentId;
    private UUID challengeId;
    private UUID challengeVersionId;
    private UUID courseCohortId;
    private Integer attemptNumber;
    private AttemptStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime completedAt;
}

package com.tpi.challengeengine.model;

import com.tpi.challengeengine.enums.OutboxEventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representara los eventos confiables que seran publicados posteriormente
 * mediante el patron Outbox. En esta etapa solo define el modelo conceptual,
 * sin persistencia, serializacion, publicacion ni reintentos.
 */
public class OutboxEvent {

    private UUID id;
    private String aggregateType;
    private UUID aggregateId;
    private String eventType;
    private String payload;
    private OutboxEventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}

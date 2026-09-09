package com.tpi.challengeengine.outbox.domain.model;

import com.tpi.challengeengine.outbox.domain.enums.OutboxEventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa un evento pendiente o ya publicado mediante el patron Outbox.
 * En futuras iteraciones se almacenara en la misma transaccion que el cambio
 * de negocio para permitir publicacion confiable hacia Kafka. En esta etapa
 * solo se define su estructura, sin serializacion, publicacion ni reintentos.
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

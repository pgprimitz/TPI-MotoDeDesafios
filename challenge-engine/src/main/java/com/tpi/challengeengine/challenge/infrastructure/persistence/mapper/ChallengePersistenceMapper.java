package com.tpi.challengeengine.challenge.infrastructure.persistence.mapper;

import com.tpi.challengeengine.challenge.domain.model.Challenge;
import com.tpi.challengeengine.challenge.infrastructure.persistence.entity.ChallengeJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce la representacion de dominio de un desafio hacia y desde su modelo
 * JPA, sin aplicar validaciones ni reglas de negocio.
 */
@Component
public class ChallengePersistenceMapper {

    public ChallengeJpaEntity toEntity(Challenge challenge) {
        return new ChallengeJpaEntity(
                challenge.getId(),
                challenge.getAuthorId(),
                challenge.getType(),
                challenge.getStatus(),
                challenge.getCurrentVersion(),
                challenge.getCreatedAt(),
                challenge.getUpdatedAt(),
                challenge.getArchivedAt()
        );
    }

    public Challenge toDomain(ChallengeJpaEntity entity) {
        return new Challenge(
                entity.getId(),
                entity.getAuthorId(),
                entity.getType(),
                entity.getStatus(),
                entity.getCurrentVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getArchivedAt()
        );
    }
}

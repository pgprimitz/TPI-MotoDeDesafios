package com.tpi.challengeengine.challenge.infrastructure.persistence.mapper;

import com.tpi.challengeengine.challenge.domain.model.ChallengeVersion;
import com.tpi.challengeengine.challenge.infrastructure.persistence.entity.ChallengeVersionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce versiones de desafio entre el dominio y JPA, preservando el vinculo
 * por identificador y sin incorporar reglas de versionado.
 */
@Component
public class ChallengeVersionPersistenceMapper {

    public ChallengeVersionJpaEntity toEntity(ChallengeVersion version) {
        return new ChallengeVersionJpaEntity(
                version.getId(),
                version.getChallengeId(),
                version.getVersionNumber(),
                version.getTitle(),
                version.getDescription(),
                version.getDifficulty(),
                version.getContentReference(),
                version.getCreatedAt()
        );
    }

    public ChallengeVersion toDomain(ChallengeVersionJpaEntity entity) {
        return new ChallengeVersion(
                entity.getId(),
                entity.getChallengeId(),
                entity.getVersionNumber(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDifficulty(),
                entity.getContentReference(),
                entity.getCreatedAt()
        );
    }
}

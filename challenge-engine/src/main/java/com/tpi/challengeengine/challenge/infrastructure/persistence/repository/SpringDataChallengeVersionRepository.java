package com.tpi.challengeengine.challenge.infrastructure.persistence.repository;

import com.tpi.challengeengine.challenge.infrastructure.persistence.entity.ChallengeVersionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Acceso Spring Data a versiones de desafio mediante las consultas simples
 * requeridas por el contrato de dominio.
 */
public interface SpringDataChallengeVersionRepository
        extends JpaRepository<ChallengeVersionJpaEntity, UUID> {

    List<ChallengeVersionJpaEntity> findByChallengeId(UUID challengeId);

    boolean existsByChallengeIdAndVersionNumber(UUID challengeId, Integer versionNumber);
}

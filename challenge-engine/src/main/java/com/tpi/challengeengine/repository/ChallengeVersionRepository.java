package com.tpi.challengeengine.repository;

import com.tpi.challengeengine.entities.ChallengeVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data para versiones de desafio, con las consultas simples
 * necesarias para acceder a su historial.
 */
public interface ChallengeVersionRepository extends JpaRepository<ChallengeVersionEntity, UUID> {

    List<ChallengeVersionEntity> findByChallengeId(UUID challengeId);

    boolean existsByChallengeIdAndVersionNumber(UUID challengeId, Integer versionNumber);
}

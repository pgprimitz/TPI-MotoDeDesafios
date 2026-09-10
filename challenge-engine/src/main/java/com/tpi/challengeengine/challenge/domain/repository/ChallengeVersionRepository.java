package com.tpi.challengeengine.challenge.domain.repository;

import com.tpi.challengeengine.challenge.domain.model.ChallengeVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia de las versiones historicas de un desafio. Expone
 * solo las operaciones tecnicas necesarias en esta etapa.
 */
public interface ChallengeVersionRepository {

    ChallengeVersion save(ChallengeVersion challengeVersion);

    Optional<ChallengeVersion> findById(UUID id);

    List<ChallengeVersion> findByChallengeId(UUID challengeId);

    boolean existsByChallengeIdAndVersionNumber(UUID challengeId, Integer versionNumber);
}

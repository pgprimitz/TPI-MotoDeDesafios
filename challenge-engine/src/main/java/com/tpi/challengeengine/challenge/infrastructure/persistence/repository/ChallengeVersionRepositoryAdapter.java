package com.tpi.challengeengine.challenge.infrastructure.persistence.repository;

import com.tpi.challengeengine.challenge.domain.model.ChallengeVersion;
import com.tpi.challengeengine.challenge.domain.repository.ChallengeVersionRepository;
import com.tpi.challengeengine.challenge.infrastructure.persistence.mapper.ChallengeVersionPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementa el contrato de persistencia de versiones con Spring Data, sin
 * decidir como se crean ni evolucionan las versiones del catalogo.
 */
@Repository
public class ChallengeVersionRepositoryAdapter implements ChallengeVersionRepository {

    private final SpringDataChallengeVersionRepository repository;
    private final ChallengeVersionPersistenceMapper mapper;

    public ChallengeVersionRepositoryAdapter(
            SpringDataChallengeVersionRepository repository,
            ChallengeVersionPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ChallengeVersion save(ChallengeVersion challengeVersion) {
        return mapper.toDomain(repository.save(mapper.toEntity(challengeVersion)));
    }

    @Override
    public Optional<ChallengeVersion> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ChallengeVersion> findByChallengeId(UUID challengeId) {
        return repository.findByChallengeId(challengeId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByChallengeIdAndVersionNumber(UUID challengeId, Integer versionNumber) {
        return repository.existsByChallengeIdAndVersionNumber(challengeId, versionNumber);
    }
}

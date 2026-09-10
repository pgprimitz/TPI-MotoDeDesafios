package com.tpi.challengeengine.challenge.infrastructure.persistence.repository;

import com.tpi.challengeengine.challenge.domain.model.Challenge;
import com.tpi.challengeengine.challenge.domain.repository.ChallengeRepository;
import com.tpi.challengeengine.challenge.infrastructure.persistence.mapper.ChallengePersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementa el contrato de persistencia de desafios mediante Spring Data y
 * mantiene la traduccion aislada en el mapper de infraestructura.
 */
@Repository
public class ChallengeRepositoryAdapter implements ChallengeRepository {

    private final SpringDataChallengeRepository repository;
    private final ChallengePersistenceMapper mapper;

    public ChallengeRepositoryAdapter(
            SpringDataChallengeRepository repository,
            ChallengePersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Challenge save(Challenge challenge) {
        return mapper.toDomain(repository.save(mapper.toEntity(challenge)));
    }

    @Override
    public Optional<Challenge> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Challenge> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }
}

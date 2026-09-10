package com.tpi.challengeengine.challenge.infrastructure.persistence.repository;

import com.tpi.challengeengine.challenge.infrastructure.persistence.entity.ChallengeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Acceso Spring Data a la tabla {@code challenge}.
 */
public interface SpringDataChallengeRepository extends JpaRepository<ChallengeJpaEntity, UUID> {
}

package com.tpi.challengeengine.repository;

import com.tpi.challengeengine.entities.ChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio Spring Data para la persistencia de desafios.
 */
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, UUID> {
}

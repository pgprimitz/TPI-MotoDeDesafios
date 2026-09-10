package com.tpi.challengeengine.challenge.domain.repository;

import com.tpi.challengeengine.challenge.domain.model.Challenge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrato de persistencia del dominio para la identidad estable de los
 * desafios. No define reglas de catalogo ni detalles de almacenamiento.
 */
public interface ChallengeRepository {

    Challenge save(Challenge challenge);

    Optional<Challenge> findById(UUID id);

    List<Challenge> findAll();

    boolean existsById(UUID id);
}

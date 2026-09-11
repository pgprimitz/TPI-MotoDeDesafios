package com.tpi.challengeengine.repository;

import com.tpi.challengeengine.entities.ChallengeEntity;
import com.tpi.challengeengine.entities.ChallengeVersionEntity;
import com.tpi.challengeengine.enums.ChallengeDifficulty;
import com.tpi.challengeengine.enums.ChallengeStatus;
import com.tpi.challengeengine.enums.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class ChallengePersistenceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer POSTGRESQL =
            new PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"));

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChallengeVersionRepository challengeVersionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        challengeVersionRepository.deleteAll();
        challengeRepository.deleteAll();
    }

    @Test
    void shouldPersistChallenge() {
        ChallengeEntity challenge = createChallenge();

        ChallengeEntity saved = challengeRepository.save(challenge);

        assertThat(challengeRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(ChallengeEntity::getAuthorId, ChallengeEntity::getType, ChallengeEntity::getStatus)
                .containsExactly(challenge.getAuthorId(), ChallengeType.THEORETICAL, ChallengeStatus.DRAFT);
    }

    @Test
    void shouldPersistVersionForExistingChallenge() {
        ChallengeEntity challenge = challengeRepository.save(createChallenge());
        ChallengeVersionEntity version = createVersion(challenge.getId(), UUID.randomUUID(), 1);

        ChallengeVersionEntity saved = challengeVersionRepository.save(version);

        assertThat(challengeVersionRepository.findByChallengeId(challenge.getId()))
                .extracting(ChallengeVersionEntity::getId)
                .containsExactly(saved.getId());
        assertThat(challengeVersionRepository.existsByChallengeIdAndVersionNumber(challenge.getId(), 1))
                .isTrue();
    }

    @Test
    void shouldRejectDuplicatedVersionNumberForSameChallenge() {
        ChallengeEntity challenge = challengeRepository.save(createChallenge());
        challengeVersionRepository.save(createVersion(challenge.getId(), UUID.randomUUID(), 1));
        ChallengeVersionEntity duplicate = createVersion(challenge.getId(), UUID.randomUUID(), 1);

        assertThatThrownBy(() -> challengeVersionRepository.save(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void shouldCreateCatalogTablesWithFlyway() {
        String challengeTable = jdbcTemplate.queryForObject(
                "SELECT to_regclass('public.challenge')",
                String.class
        );
        String challengeVersionTable = jdbcTemplate.queryForObject(
                "SELECT to_regclass('public.challenge_version')",
                String.class
        );

        assertThat(challengeTable).isEqualTo("challenge");
        assertThat(challengeVersionTable).isEqualTo("challenge_version");
    }

    private ChallengeEntity createChallenge() {
        LocalDateTime now = LocalDateTime.now();
        return new ChallengeEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                ChallengeType.THEORETICAL,
                ChallengeStatus.DRAFT,
                1,
                now,
                now,
                null
        );
    }

    private ChallengeVersionEntity createVersion(UUID challengeId, UUID versionId, int versionNumber) {
        return new ChallengeVersionEntity(
                versionId,
                challengeId,
                versionNumber,
                "Introduccion a Java",
                "Descripcion inicial",
                ChallengeDifficulty.BASIC,
                "t04:content:java-intro",
                LocalDateTime.now()
        );
    }
}

package com.tpi.challengeengine.challenge.infrastructure.persistence;

import com.tpi.challengeengine.challenge.domain.enums.ChallengeDifficulty;
import com.tpi.challengeengine.challenge.domain.enums.ChallengeStatus;
import com.tpi.challengeengine.challenge.domain.enums.ChallengeType;
import com.tpi.challengeengine.challenge.domain.model.Challenge;
import com.tpi.challengeengine.challenge.domain.model.ChallengeVersion;
import com.tpi.challengeengine.challenge.domain.repository.ChallengeRepository;
import com.tpi.challengeengine.challenge.domain.repository.ChallengeVersionRepository;
import com.tpi.challengeengine.challenge.infrastructure.persistence.repository.SpringDataChallengeRepository;
import com.tpi.challengeengine.challenge.infrastructure.persistence.repository.SpringDataChallengeVersionRepository;
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
    private SpringDataChallengeRepository springDataChallengeRepository;

    @Autowired
    private SpringDataChallengeVersionRepository springDataChallengeVersionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        springDataChallengeVersionRepository.deleteAll();
        springDataChallengeRepository.deleteAll();
    }

    @Test
    void shouldPersistChallenge() {
        Challenge challenge = createChallenge();

        Challenge saved = challengeRepository.save(challenge);

        assertThat(challengeRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Challenge::getAuthorId, Challenge::getType, Challenge::getStatus)
                .containsExactly(challenge.getAuthorId(), ChallengeType.THEORETICAL, ChallengeStatus.DRAFT);
    }

    @Test
    void shouldPersistVersionForExistingChallenge() {
        Challenge challenge = challengeRepository.save(createChallenge());
        ChallengeVersion version = createVersion(challenge.getId(), UUID.randomUUID(), 1);

        ChallengeVersion saved = challengeVersionRepository.save(version);

        assertThat(challengeVersionRepository.findByChallengeId(challenge.getId()))
                .extracting(ChallengeVersion::getId)
                .containsExactly(saved.getId());
        assertThat(challengeVersionRepository.existsByChallengeIdAndVersionNumber(challenge.getId(), 1))
                .isTrue();
    }

    @Test
    void shouldRejectDuplicatedVersionNumberForSameChallenge() {
        Challenge challenge = challengeRepository.save(createChallenge());
        challengeVersionRepository.save(createVersion(challenge.getId(), UUID.randomUUID(), 1));
        ChallengeVersion duplicate = createVersion(challenge.getId(), UUID.randomUUID(), 1);

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

    private Challenge createChallenge() {
        LocalDateTime now = LocalDateTime.now();
        return new Challenge(
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

    private ChallengeVersion createVersion(UUID challengeId, UUID versionId, int versionNumber) {
        return new ChallengeVersion(
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

CREATE TABLE challenge (
    id UUID PRIMARY KEY,
    author_id UUID,
    type VARCHAR(32),
    status VARCHAR(32),
    current_version INTEGER,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    archived_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE challenge_version (
    id UUID PRIMARY KEY,
    challenge_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    title VARCHAR(255),
    description VARCHAR(2000),
    difficulty VARCHAR(32),
    content_reference VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_challenge_version_challenge
        FOREIGN KEY (challenge_id) REFERENCES challenge (id),
    CONSTRAINT uk_challenge_version_number
        UNIQUE (challenge_id, version_number)
);

CREATE INDEX idx_challenge_author_id ON challenge (author_id);
CREATE INDEX idx_challenge_status ON challenge (status);
CREATE INDEX idx_challenge_type ON challenge (type);

-- The unique index backing uk_challenge_version_number also supports lookups
-- by its leading challenge_id column, so an additional index is unnecessary.

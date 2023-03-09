CREATE TABLE IF NOT EXISTS "feed"
(
    id              BIGSERIAL PRIMARY KEY,
    author_id       BIGSERIAL   NOT NULL REFERENCES "user" (id),
    description     TEXT        NOT NULL,
    medias          jsonb       NOT NULL,
    total_favorites INTEGER     NOT NULL,
    total_comments  INTEGER     NOT NULL,
    total_reposts   INTEGER     NOT NULL,
    language        VARCHAR(16) NULL,
    created_at      timestamptz   NOT NULL,
    updated_at      timestamptz NOT NULL,
    CHECK ( total_favorites >= 0 AND total_comments >= 0 AND total_reposts >= 0)
)
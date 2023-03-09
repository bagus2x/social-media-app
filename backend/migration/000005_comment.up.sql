CREATE TABLE "comment"
(
    id              BIGSERIAL PRIMARY KEY,
    feed_id         BIGINT        NOT NULL REFERENCES feed (id),
    parent_id       BIGINT        NULL REFERENCES comment (id),
    path            ltree         NOT NULL,
    author_id       BIGINT        NOT NULL REFERENCES "user" (id),
    description     VARCHAR(1024) NOT NULL,
    medias          jsonb         NOT NULL,
    total_favorites INTEGER       NOT NULL,
    total_replies   INTEGER       NOT NULL,
    created_at      timestamptz   NOT NULL,
    CHECK ( total_favorites >= 0 AND total_replies >= 0 )
)
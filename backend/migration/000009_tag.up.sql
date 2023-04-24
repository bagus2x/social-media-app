CREATE TABLE IF NOT EXISTS tag
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    feed_id    BIGINT       NOT NULL REFERENCES feed (id) ON DELETE CASCADE,
    comment_id BIGINT       NULL REFERENCES comment (id) ON DELETE CASCADE,
    name       VARCHAR(128) NOT NULL,
    type       VARCHAR(32)  NOT NULL,
    country    VARCHAR(128) NULL,
    created_at timestamptz  NOT NULL
)
CREATE TABLE IF NOT EXISTS tag
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL REFERENCES "user" (id),
    feed_id    BIGINT       NOT NULL REFERENCES feed (id),
    comment_id BIGINT       NULL REFERENCES comment (id),
    name       VARCHAR(128) NOT NULL,
    type       VARCHAR(32)  NOT NULL,
    country    VARCHAR(128) NULL,
    created_at timestamptz  NOT NULL
)
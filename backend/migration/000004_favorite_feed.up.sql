CREATE TABLE IF NOT EXISTS "favorite_feed"
(
    liker_id   BIGSERIAL   NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    feed_id    BIGSERIAL   NOT NULL REFERENCES "feed" (id) ON DELETE CASCADE,
    created_at timestamptz NOT NULL,
    PRIMARY KEY (liker_id, feed_id)
)
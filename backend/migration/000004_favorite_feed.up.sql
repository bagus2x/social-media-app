CREATE TABLE IF NOT EXISTS "favorite_feed"
(
    liker_id   BIGSERIAL   NOT NULL REFERENCES "user" (id),
    feed_id    BIGSERIAL   NOT NULL REFERENCES "feed" (id),
    created_at timestamptz NOT NULL,
    PRIMARY KEY (liker_id, feed_id)
)
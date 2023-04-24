CREATE TABLE IF NOT EXISTS "followed_user"
(
    followed_id BIGSERIAL REFERENCES "user" (id) ON DELETE CASCADE,
    follower_id BIGSERIAL REFERENCES "user" (id) ON DELETE CASCADE,
    created_at  timestamptz NOT NULL,
    PRIMARY KEY (followed_id, follower_id)
)
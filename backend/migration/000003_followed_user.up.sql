CREATE TABLE IF NOT EXISTS "followed_user"
(
    followed_id BIGSERIAL REFERENCES "user" (id),
    follower_id BIGSERIAL REFERENCES "user" (id),
    created_at  timestamptz NOT NULL,
    PRIMARY KEY (followed_id, follower_id)
)
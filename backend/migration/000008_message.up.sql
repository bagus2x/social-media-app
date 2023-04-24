CREATE TABLE IF NOT EXISTS message
(
    id          BIGSERIAL PRIMARY KEY,
    chat_id     BIGINT      NOT NULL REFERENCES chat (id) ON DELETE CASCADE,
    sender_id   BIGINT      NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    description TEXT        NOT NULL,
    medias      jsonb       NOT NULL,
    created_at  timestamptz NOT NULL
)
CREATE TABLE member
(
    user_id    BIGINT      NOT NULL REFERENCES "user" (id),
    chat_id    BIGINT      NOT NULL REFERENCES chat (id) ON DELETE CASCADE,
    created_at timestamptz NOT NULL,
    PRIMARY KEY (user_id, chat_id)
)
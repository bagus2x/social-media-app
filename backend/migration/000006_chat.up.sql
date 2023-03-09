CREATE TYPE "chat_type" AS ENUM ('group', 'private');

CREATE TABLE chat
(
    id                   BIGSERIAL PRIMARY KEY,
    creator_id           BIGINT       NOT NULL REFERENCES "user" (id),
    name                 VARCHAR(128) NULL,
    photo                VARCHAR(512) NULL,
    "type"               chat_type    NOT NULL,
    private_chat_id      VARCHAR(256) NULL UNIQUE,
    created_at           timestamptz  NOT NULL,
    updated_at           timestamptz  NOT NULL,
    last_message_sent_at timestamptz  NOT NULL NULL
)
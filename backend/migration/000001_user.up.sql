CREATE TABLE IF NOT EXISTS "user"
(
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(128) NOT NULL,
    name            VARCHAR(256) NOT NULL,
    email           VARCHAR(256) NOT NULL UNIQUE,
    password        VARCHAR(512) NOT NULL,
    photo           VARCHAR(512) NULL,
    header          VARCHAR(512) NULL,
    bio             VARCHAR(512) NULL,
    location        VARCHAR(128) NULL,
    website         VARCHAR(512) NULL,
    verified        BOOLEAN      NOT NULL NULL,
    date_of_birth   VARCHAR(128) NULL,
    total_followers INTEGER      NOT NULL,
    total_following INTEGER      NOT NULL,
    fcm_token       VARCHAR(255) NULL,
    created_at      timestamptz  NOT NULL,
    updated_at      timestamptz  NOT NULL
)
create or replace function create_path(parent_id_param bigint) returns ltree
    language plpgsql
as
$$
declare
    parent_path        ltree;
    declare current_id BIGINT;
BEGIN
    SELECT currval('"comment_id_seq"') into current_id;

    IF parent_id_param is NOT NULL THEN
        SELECT path into parent_path FROM comment WHERE id = parent_id_param;
        return parent_path || current_id::text::ltree;
    end if;

    return current_id::text::ltree;
end;
$$;

CREATE TABLE IF NOT EXISTS "comment"
(
    id              BIGSERIAL PRIMARY KEY,
    feed_id         BIGINT        NOT NULL REFERENCES feed (id) ON DELETE CASCADE,
    parent_id       BIGINT        NULL REFERENCES comment (id) ON DELETE CASCADE,
    path            ltree         NOT NULL,
    author_id       BIGINT        NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    description     VARCHAR(1024) NOT NULL,
    medias          jsonb         NOT NULL,
    total_favorites INTEGER       NOT NULL,
    total_replies   INTEGER       NOT NULL,
    created_at      timestamptz   NOT NULL,
    CHECK ( total_favorites >= 0 AND total_replies >= 0 )
)
ALTER TABLE post_playlist_mapping
DROP FOREIGN KEY fk_mapping_member,
    DROP PRIMARY KEY,
DROP INDEX idx_mapping_playlist_id,
DROP COLUMN member_id,
    ADD PRIMARY KEY (playlist_id, post_id),
    ADD INDEX idx_mapping_post_id (post_id),
    ADD INDEX idx_mapping_playlist_created_at
        (playlist_id, created_at, post_id);
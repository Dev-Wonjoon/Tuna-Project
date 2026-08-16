CREATE TABLE music_sources (
    id BIGINT NOT NULL AUTO_INCREMENT,
    provider VARCHAR(32) NOT NULL,
    resource_type VARCHAR(32) NOT NULL,
    resource_id VARCHAR(255) NOT NULL,
    canonical_url VARCHAR(2048) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_music_source_identity (provider, resource_type, resource_id)
);

CREATE TABLE post_music_sources (
    id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    music_source_id BIGINT NOT NULL,
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_post_music_source
        UNIQUE (post_id, music_source_id),

    CONSTRAINT fk_post_music_source_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_music_source_source
        FOREIGN KEY (music_source_id)
        REFERENCES music_sources(id),

    INDEX idx_post_music_source_order (
        post_id, sort_order, id
    ),

    INDEX idx_post_music_source_source (music_source_id)
);
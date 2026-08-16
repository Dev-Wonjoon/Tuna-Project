INSERT INTO music_sources (
    provider,
    resource_type,
    resource_id,
    canonical_url
)
SELECT 'raw_url', 'url', SHA2(TRIM(music_url), 256), MIN(TRIM(music_url))
FROM posts
WHERE music_url IS NOT NULL
    AND TRIM(music_url) <> ''
GROUP BY SHA2(TRIM(music_url), 256);

INSERT INTO post_music_sources (post_id, music_source_id, sort_order)
SELECT p.id, ms.id, 0
FROM posts p
JOIN music_sources ms
    ON ms.provider = 'raw_url'
    AND ms.resource_type = 'url'
    AND ms.resource_id = SHA2(TRIM(p.music_url), 256)
WHERE p.music_url IS NOT NULL
    AND TRIM(p.music_url) <> '';
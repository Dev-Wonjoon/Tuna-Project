CREATE TABLE IF NOT EXISTS `members`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `email`      varchar(255) NOT NULL,
    `name`       varchar(16)  NOT NULL,
    `password`   varchar(255) NOT NULL,
    `role`       enum('ADMIN','USER') NOT NULL DEFAULT 'USER',
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `posts`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `title`      varchar(255) NOT NULL,
    `content`    text         NOT NULL,
    `music_url`  varchar(2048)         DEFAULT NULL,
    `member_id`  bigint       NOT NULL,
    `view_count` int unsigned NOT NULL DEFAULT '0',
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `title` (`title`),
    KEY          `idx_posts_member_created_at` (`member_id`,`created_at`),
    KEY          `idx_posts_created_at` (`created_at`),
    CONSTRAINT `fk_posts_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `comments`
(
    `id`         bigint   NOT NULL AUTO_INCREMENT,
    `content`    text     NOT NULL,
    `member_id`  bigint   NOT NULL,
    `post_id`    bigint   NOT NULL,
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY          `idx_comments_post_created_at` (`post_id`,`created_at`),
    KEY          `idx_comments_member_id` (`member_id`),
    CONSTRAINT `fk_comments_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_comments_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `playlists`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `member_id`  bigint       NOT NULL,
    `name`       varchar(255) NOT NULL,
    `created_at` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_playlists_member_name` (`member_id`,`name`),
    KEY          `idx_playlists_member_id` (`member_id`),
    CONSTRAINT `fk_playlists_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `post_playlist_mapping`
(
    `post_id`     bigint   NOT NULL,
    `playlist_id` bigint   NOT NULL,
    `created_at`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`post_id`, `playlist_id`),
    KEY           `idx_mapping_playlist_id` (`playlist_id`),
    CONSTRAINT `fk_mapping_playlist` FOREIGN KEY (`playlist_id`) REFERENCES `playlists` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_mapping_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

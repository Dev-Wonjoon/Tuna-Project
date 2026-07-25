CREATE TABLE IF NOT EXISTS members (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS posts (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    content TEXT NOT NULL,
    music_url VARCHAR(2048) NULL,
    member_id INT NOT NULL,
    view_count INT UNSIGNED NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_posts_member
        FOREIGN KEY (member_id)
        REFERENCES members (id)
        ON DELETE CASCADE,

    INDEX idx_posts_member_created_at (member_id, created_at),
    INDEX idx_posts_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS comments (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    member_id INT NOT NULL,
    post_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_comments_member
        FOREIGN KEY (member_id)
        REFERENCES members (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE,

    INDEX idx_comments_post_created_at (post_id, created_at),
    INDEX idx_comments_member_id (member_id)
);

CREATE TABLE IF NOT EXISTS playlists (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_playlists_member
        FOREIGN KEY (member_id)
        REFERENCES members (id)
        ON DELETE CASCADE,

    CONSTRAINT uk_playlists_member_name
        UNIQUE (member_id, name),

    INDEX idx_playlists_member_id (member_id)
);

CREATE TABLE IF NOT EXISTS post_playlist_mapping (
    post_id INT NOT NULL,
    playlist_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (post_id, playlist_id),

    CONSTRAINT fk_mapping_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_mapping_playlist
        FOREIGN KEY (playlist_id)
        REFERENCES playlists (id)
        ON DELETE CASCADE,

    INDEX idx_mapping_playlist_id (playlist_id)
);

-- 사용자
INSERT IGNORE INTO members (
       id,
       email,
       password,
       role,
       created_at
) VALUES
(
       1,
       'admin@test.com',
       '{noop}test1234',
       'ADMIN',
       NOW() - INTERVAL 30 DAY
),
(
    2,
    'user1@tuna.test',
    '{noop}test1234',
    'USER',
    NOW() - INTERVAL 20 DAY
),
(
    3,
    'user2@tuna.test',
    '{noop}test1234',
    'USER',
    NOW() - INTERVAL 10 DAY
);

-- 게시글
INSERT IGNORE INTO posts (
       id,
       title,
       content,
       member_id,
       view_count,
       created_at,
       updated_at
) VALUES
(
       101,
       '게시글 1',
       '내용 1',
       2,
       128,
       NOW() - INTERVAL 3 DAY,
       NOW() - INTERVAL 3 DAY
),
(
       102,
       '게시글 2',
       '내용 2',
       2,
       73,
       NOW() - INTERVAL 1 DAY,
       NOW() - INTERVAL 1 DAY
),
(
       103,
       '게시글 3',
       '내용 3',
       3,
       251,
       NOW() - INTERVAL 8 HOUR,
       NOW() - INTERVAL 8 HOUR
),
(
       104,
       '게시글 4',
       '내용 4',
       3,
       251,
       NOW() - INTERVAL 8 HOUR,
       NOW() - INTERVAL 8 HOUR
),
(
       105,
       '게시글 5',
       '내용 5',
       3,
       251,
       NOW() - INTERVAL 8 HOUR,
       NOW() - INTERVAL 8 HOUR
);


INSERT IGNORE INTO comments (
       id,
       content,
       member_id,
       post_id,
       created_at,
       updated_at
) VALUES
(
       1001,
       '첫 번째 댓글입니다.',
       3,
       101,
       NOW() - INTERVAL 2 DAY,
       NOW() - INTERVAL 2 DAY
),
(
       1002,
       '두 번째 댓글입니다.',
       1,
       101,
       NOW() - INTERVAL 1 DAY,
       NOW() - INTERVAL 1 DAY
);

-- 플레이리스트
INSERT IGNORE INTO playlists (
       id,
       member_id,
       name,
       created_at
) VALUES
(
       201,
       2,
       '플레이리스트 1',
       NOW() - INTERVAL 7 DAY
),
(
       202,
       2,
       '플레이리스트 2',
       NOW() - INTERVAL 5 DAY
);

-- 플레이리스트 -> 게시글
INSERT IGNORE INTO post_playlist_mapping (
       post_id,
       playlist_id,
       created_at
) VALUES
(
       103,
       201,
       NOW() - INTERVAL 7 HOUR
),
(
       101,
       202,
       NOW() - INTERVAL 3 DAY
);
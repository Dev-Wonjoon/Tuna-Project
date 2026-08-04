package net.tuna.post.repository;

import net.tuna.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcTemplatePostRepository implements PostRepository{
    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PostDto> postRowMapper = (ResultSet rs, int rowNum) -> {
        return PostDto.builder()
                //Long으로 변경
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .authorEmail(rs.getString("author_email"))
                .memberId(rs.getLong("member_id"))
                .title(rs.getString("title"))
                .content(rs.getString("content"))
                .musicUrl(rs.getString("music_url"))
                .viewCount(rs.getInt("view_count"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    };

    //전체 게시글 조회
    @Override
    public List<PostDto> findAll() {
        String sql = "SELECT p.*, m.name AS name , m.email AS author_email " +
                "FROM posts p " +
                "LEFT JOIN members m ON p.member_id = m.id " +
                "ORDER BY p.created_at DESC";
        return jdbcTemplate.query(sql, postRowMapper);
    }

    //게시물 상세보기
    @Override
    public PostDto findById(long id) {
        String sql = "SELECT p.*, m.name AS name , m.email AS author_email " +
                "FROM posts p " +
                "LEFT JOIN members m ON p.member_id = m.id " +
                "WHERE p.id = ?";
        return jdbcTemplate.queryForObject(sql,postRowMapper,id);
    }

    //멤버아이디로 게시글 찾기
    @Override
    public List<PostDto> findPostsByMemberId(long id) {
        String sql = "SELECT p.*, m.name AS name, m.email AS author_email " +
                "FROM posts p " +
                "LEFT JOIN members m ON p.member_id = m.id " +
                "WHERE p.member_id = ?";
        return jdbcTemplate.query(sql, postRowMapper, id);
    }

    //게시글 생성
    @Override
    public long createPost(PostDto post) {
        String sql = "INSERT INTO posts(title,content,music_url,member_id) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, post.getTitle());
                    pstmt.setString(2, post.getContent());
                    pstmt.setString(3, post.getMusicUrl());
                    pstmt.setLong(4, post.getMemberId());
                    return pstmt;
                }, keyHolder);

        if(keyHolder.getKey() != null ){
            return keyHolder.getKey().longValue();
        }
        throw new RuntimeException("게시글을 저장하지 못했습니다..");
//        jdbcTemplate.update(sql
//                , post.getTitle()
//                , post.getContent()
//                , post.getMusicUrl()
//                , post.getMemberId());
    }

    //게시글 수정
    @Override
    public void updatePost(PostDto post) {
        String sql ="UPDATE posts SET title = ?, content = ?, music_url = ?, updated_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql
                , post.getTitle()
                , post.getContent()
                , post.getMusicUrl()
                , post.getId());
    }


    //게시글 삭제
    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }


    //게시글 조회수 증가
    @Override
    public void addViewCount(long id) {
        String sql = "UPDATE posts SET view_count = view_count + 1 WHERE id =?";
        jdbcTemplate.update(sql,id);
    }

    @Override
    public List<PostDto> findByKeywordFromTitle(String keyword) {
        String sql = """
                SELECT p.*, m.name AS name , m.email AS author_email
                FROM posts p
                LEFT JOIN members m ON p.member_id = m.id
                WHERE p.title LIKE ?
                ORDER BY p.created_at DESC
                """;
        String searchPattern = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, postRowMapper, searchPattern);
    }

    @Override
    public List<PostDto> findByKeywordFromContent(String keyword) {
        String sql = """
                SELECT p.*, m.name AS name , m.email AS author_email
                FROM posts p
                LEFT JOIN members m ON p.member_id = m.id
                WHERE p.content LIKE ?
                ORDER BY p.created_at DESC
                """;
        String searchPattern = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, postRowMapper, searchPattern);
    }

    @Override
    public List<PostDto> findByKeywordFromTitleContent(String keyword) {
        String sql = """
                SELECT p.*, m.name AS name , m.email AS author_email
                FROM posts p
                LEFT JOIN members m ON p.member_id = m.id
                WHERE p.title LIKE ? OR p.content LIKE ?
                ORDER BY p.created_at DESC
                """;
        String searchPattern = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, postRowMapper, searchPattern, searchPattern);
    }

    @Override
    public List<PostDto> findByKeywordFromAuthor(String keyword) {
        String sql = """
                SELECT p.*, m.name AS name , m.email AS author_email
                FROM posts p
                LEFT JOIN members m ON p.member_id = m.id
                WHERE name LIKE ?
                ORDER BY p.created_at DESC
                """;
        String searchPattern = "%" + keyword.trim() + "%";
        return jdbcTemplate.query(sql, postRowMapper, searchPattern);
    }
}

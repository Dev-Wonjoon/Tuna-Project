package net.tuna.post.repository;

import net.tuna.post.dto.PostDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
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
    public void createPost(PostDto post) {
        String sql = "INSERT INTO posts(title,content,music_url,member_id) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql
                , post.getTitle()
                , post.getContent()
                , post.getMusicUrl()
                , post.getMemberId());
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

    //게시글 내 댓글 조회
    @Override
    public List<Map<String, Object>> findCommentsById(long id) {
        String sql = "SELECT c.*, m.name AS name, m.email As author_email " +
                "FROM comments c " +
                "LEFT JOIN members m ON c.member_id = m.id " +
                "WHERE c.post_id = ?";
        return jdbcTemplate.queryForList(sql,id);
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


}

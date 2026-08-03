package net.tuna.comment.repository;

import lombok.RequiredArgsConstructor;
import net.tuna.comment.dto.CommentDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcTemplateCommentRepository implements CommentRepository{
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<CommentDto> commentDtoRowMapper = (rs, rowNum) -> {
        return CommentDto.builder()
                .id(rs.getLong("id"))
                .content(rs.getString("content"))
                .memberId(rs.getLong("member_id"))
                .postId(rs.getLong("post_id"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    };

    @Override
    public List<CommentDto> findByPostId(Long postId) {
        return List.of();
    }

    @Override
    public int createComment(CommentDto comment) {
        String sql = "INSERT INTO comments (content, member_id, post_id) VALUES (?, ?, ?)";

        return jdbcTemplate.update(sql,
                comment.getContent(),
                comment.getMemberId(),
                comment.getPostId()
        );
    }

    @Override
    public int updateComment(CommentDto comment) {
        return 0;
    }

    @Override
    public CommentDto findById(long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, commentDtoRowMapper, id);
    }

    @Override
    public int deleteById(long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}

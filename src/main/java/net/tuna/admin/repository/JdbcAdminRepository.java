package net.tuna.admin.repository;

import lombok.RequiredArgsConstructor;
import net.tuna.admin.dto.AdminMemberDto;
import net.tuna.member.dto.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcAdminRepository implements AdminRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<AdminMemberDto> adminMemberRowMapper = (rs, rowNum) ->
            new AdminMemberDto(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("name"),
                    Role.valueOf(rs.getString("role")),
                    rs.getObject("created_at", LocalDateTime.class),
                    rs.getInt("post_count")
            );

    @Override
    public List<AdminMemberDto> findAllMembers() {
        String sql = """
        SELECT
            m.id,
            m.email,
            m.name,
            m.role,
            m.created_at,
            COUNT(DISTINCT p.id) AS post_count
        FROM members m
        LEFT JOIN posts p
            ON m.id = p.member_id
        GROUP BY
            m.id,
            m.email,
            m.name,
            m.role,
            m.created_at
        """;

        return jdbcTemplate.query(sql, adminMemberRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        String sql = """
                DELETE FROM members
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);
    }
}
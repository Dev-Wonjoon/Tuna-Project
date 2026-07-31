package net.tuna.member.repository;

import net.tuna.member.dto.MemberDto;
import net.tuna.member.dto.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MemberDto> memberRowMapper = (rs, rowNum) -> {
        MemberDto member = new MemberDto();

        member.setId(rs.getLong("id"));
        member.setEmail(rs.getString("email"));
        member.setPassword(rs.getString("password"));
        member.setName((rs.getString("name")));
        member.setImageUrl((rs.getString("image_url")));
        member.setRole(Role.valueOf(rs.getString("role")));
        member.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));

        return member;
    };

    @Override
    public MemberDto findByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email = ?";

        List<MemberDto> result = jdbcTemplate.query(sql, memberRowMapper, email);

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public int save(MemberDto memberDto) {
        return jdbcTemplate.update(
                "INSERT INTO members (email, password, name, image_url, role) VALUES (?, ?, ?, ?, ?)",
                memberDto.getEmail(),
                memberDto.getPassword(),
                memberDto.getName(),
                memberDto.getImageUrl(),
                memberDto.getRole().name()
        );
    }

    @Override
    public MemberDto findById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM members WHERE id = ?", memberRowMapper, id);
    }

    @Override
    public int saveAdmin(MemberDto memberDto) {
        return jdbcTemplate.update(
                "INSERT INTO members (email, password, name, image_url, role) VALUES (?, ? , ?, ?, ?)",
                memberDto.getEmail(), memberDto.getPassword(), memberDto.getName(), memberDto.getImageUrl(), Role.ADMIN.name()
        );
    }

    @Override
    public int updateRole(MemberDto memberDto) {
        String sql = """
        UPDATE members
        SET role = ?
        WHERE id = ?
        """;

        return jdbcTemplate.update(
                sql,
                memberDto.getRole().name(),
                memberDto.getId()
        );
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
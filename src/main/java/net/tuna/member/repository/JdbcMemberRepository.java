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
                "INSERT INTO members (email, password, role) VALUES (?, ?, ?)",
                memberDto.getEmail(), memberDto.getPassword(), memberDto.getRole().name()
        );
    }

    @Override
    public MemberDto findById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM members WHERE id = ?", memberRowMapper, id);
    }

    @Override
    public int saveAdmin(MemberDto memberDto) {
        return jdbcTemplate.update(
                "INSERT INTO members (email, password, role) VALUES (?, ? , ?)",
                memberDto.getEmail(), memberDto.getPassword(), Role.ADMIN.name()
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
}
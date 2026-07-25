package net.tuna.mock;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Profile("mock")
@Controller
public class MockHomeController {

    private final JdbcTemplate jdbcTemplate;

    public MockHomeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Map<String, Object>> posts = jdbcTemplate.queryForList("""
            SELECT
                p.id,
                p.title,
                p.content,
                p.view_count AS viewCount,
                p.created_at AS createdAt,
                m.email AS authorEmail
            FROM posts p
            JOIN members m
                ON m.id = p.member_id
            ORDER BY p.created_at DESC
        """);

        List<Map<String, Object>> playlists = jdbcTemplate.queryForList("""
            SELECT
                pl.id,
                pl.name,
                COUNT(ppm.post_id) AS postCount
            FROM playlists pl
            LEFT JOIN post_playlist_mapping ppm
                ON ppm.playlist_id = pl.id
            WHERE pl.member_id = ?
            GROUP BY pl.id, pl.name, pl.created_at
            ORDER BY pl.created_at DESC
        """, 2);

        model.addAttribute("title", "Tuna");
        model.addAttribute("posts", posts);
        model.addAttribute("playlists", playlists);
        model.addAttribute("currentMenu", "home");
        model.addAttribute("currentPlaylistId", null);

        return "pages/home";
    }
}

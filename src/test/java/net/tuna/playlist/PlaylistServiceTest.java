package net.tuna.playlist;

import net.tuna.playlist.dto.Playlist;
import net.tuna.playlist.repository.PlaylistPostRepository;
import net.tuna.playlist.repository.PlaylistRepository;
import net.tuna.playlist.service.PlaylistService;
import net.tuna.post.dto.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlaylistServiceTest {

    RecordingPlaylistRepository playlistRepository;
    RecordingPlaylistPostRepository playlistPostRepository;
    PlaylistService playlistService;

    @BeforeEach
    void setup() {
        playlistRepository = new RecordingPlaylistRepository();
        playlistPostRepository = new RecordingPlaylistPostRepository();


        playlistService = new PlaylistService(playlistRepository, playlistPostRepository);

    }

    @Test
    void 플레이리스트를_생성한다() {
        // given
        long memberId = 1L;

        Playlist playlist = new Playlist();
        playlist.setName("테스트할 때");
        playlist.setMemberId(memberId);

        // when
        playlistService.createPlaylist(playlist);

        // then
        assertNotNull(playlistRepository.savedPlaylist);

        assertEquals(
                "테스트할 때",
                playlistRepository.savedPlaylist.getName()
        );

        assertEquals(
                memberId,
                playlistRepository.savedMemberId
        );
    }

    @Test
    void 회원이_특정_플레이리스트를_조회한다() {
        // given
        long memberId = 1L;
        long playlistId = 10L;

        playlistRepository.playlistToReturn =
                new Playlist(
                        playlistId,
                        memberId,
                        "조회 테스트를 할 때",
                        LocalDateTime.now()
                );

        // when
        Playlist result =
                playlistService.getPlaylistById(
                        playlistId,
                        memberId
                );

        // then
        assertNotNull(result);
        assertEquals(playlistId, result.getId());
        assertEquals(memberId, result.getMemberId());
        assertEquals("조회 테스트를 할 때", result.getName());

        assertEquals(
                playlistId,
                playlistRepository.requestedPlaylistId
        );
    }

    @Test
    void 플레이리스트에_포함된_게시글을_조회한다() {
        // given
        long playlistId = 10L;
        long memberId = 1L;

        PostDto postDto = PostDto.builder()
                .id(100L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .build();

        playlistPostRepository.postsToReturn =
                List.of(postDto);

        // when
        List<PostDto> result = playlistService.getPosts(
                playlistId,
                memberId
        );

        // then
        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
        assertEquals("테스트 게시글", result.get(0).getTitle());

        assertEquals(
                playlistId,
                playlistPostRepository.requestedPlaylistId
        );

        assertEquals(
                memberId,
                playlistPostRepository.requestedMemberId
        );
    }


    private static class RecordingPlaylistRepository implements PlaylistRepository {
        private Playlist savedPlaylist;
        private long savedMemberId;

        private List<Playlist> playlistsToReturn = List.of();

        private Playlist playlistToReturn;

        private long requestedMemberId;
        private long requestedPlaylistId;

        @Override
        public List<Playlist> findAll(long memberId) {
            this.requestedMemberId = memberId;
            return playlistsToReturn;
        }

        @Override
        public Playlist findById(long PlaylistId, long memberId) {
            this.requestedPlaylistId = PlaylistId;
            this.requestedMemberId = memberId;

            return playlistToReturn;
        }

        @Override
        public void save(Playlist playlist) {
            this.savedPlaylist = playlist;
            this.savedMemberId = playlist.getMemberId();
        }

        @Override
        public void deleteById(long id, long memberId) {

        }

    }

    private static class RecordingPlaylistPostRepository implements PlaylistPostRepository {

        private List<PostDto> postsToReturn = List.of();

        private long requestedPlaylistId;
        private long requestedMemberId;

        @Override
        public List<PostDto> findAllByPlaylistId(long playlistId, long memberId) {
            this.requestedPlaylistId = playlistId;
            this.requestedMemberId = memberId;

            return postsToReturn;
        }
    }
}

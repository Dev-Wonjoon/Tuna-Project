package net.tuna.mypage.service;

import lombok.RequiredArgsConstructor;
import net.tuna.member.dto.MemberDto;
import net.tuna.member.repository.MemberRepository;
import net.tuna.member.security.CustomUserDetails;
import net.tuna.post.dto.PostDto;
import net.tuna.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public MemberDto getMypage(CustomUserDetails user) {
        return memberRepository.findById(user.getMember().getId());
    }

    public List<PostDto> getMyPosts(long memberId) {

        List<PostDto> posts = postRepository.findPostsByMemberId(memberId);

        for (PostDto post : posts) {

            String youtubeId = extractYoutubeId(post.getMusicUrl());

            if (youtubeId != null) {

                post.setThumbnailUrl(
                        "https://img.youtube.com/vi/"
                                + youtubeId
                                + "/maxresdefault.jpg" //최대화질 이미지 불러오기
                );
            }
        }

        return posts;
    }

    private String extractYoutubeId(String url) {

        if (url == null || url.isEmpty()) {
            return null;
        }

        // https://www.youtube.com/watch?v=abc123
        if (url.contains("v=")) {

            return url.substring(url.indexOf("v=") + 2)
                    .split("&")[0];
        }

        // https://youtu.be/abc123
        if (url.contains("youtu.be/")) {

            return url.substring(url.indexOf("youtu.be/") + 9)
                    .split("\\?")[0];
        }

        return null;
    }
}

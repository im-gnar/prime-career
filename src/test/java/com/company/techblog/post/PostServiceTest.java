package com.company.techblog.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import com.company.techblog.domain.Post;
import com.company.techblog.domain.User;
import com.company.techblog.dto.PostDto;
import com.company.techblog.repository.PostRepository;
import com.company.techblog.repository.UserRepository;
import com.company.techblog.service.PostService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @DisplayName("활성 사용자는 게시글을 작성할 수 있다")
    @Test
    void activeUserCanCreatePost() {
        // given
        Long userId = 1L;
        PostDto.Request request = new PostDto.Request();
        request.setUserId(userId);
        request.setTitle("테스트 제목");
        request.setContent("테스트 내용");

        User activeUser = User.builder()
            .email("test@example.com")
            .name("테스터")
            .build();

        Post savedPost = Post.builder()
            .author(activeUser)
            .title(request.getTitle())
            .content(request.getContent())
            .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(activeUser));
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        // when
        PostDto.Response response = postService.createPost(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(request.getTitle());
        assertThat(response.getContent()).isEqualTo(request.getContent());

        verify(userRepository).findById(userId);
        verify(postRepository).save(any(Post.class));
    }

    @DisplayName("비활성 사용자는 게시글 작성 시 예외가 발생한다")
    @Test
    void inactiveUserCannotCreatePost() {
        // given
        Long userId = 2L;
        PostDto.Request request = new PostDto.Request();
        request.setUserId(userId);
        request.setTitle("테스트 제목");
        request.setContent("테스트 내용");

        User inactiveUser = User.builder()
            .email("inactive@example.com")
            .name("퇴사자")
            .build();
        inactiveUser.resign();

        given(userRepository.findById(userId)).willReturn(Optional.of(inactiveUser));

        // when & then
        assertThatThrownBy(() -> postService.createPost(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Inactive user cannot create posts");

        verify(userRepository).findById(userId);
        verify(postRepository, never()).save(any(Post.class));
    }
}
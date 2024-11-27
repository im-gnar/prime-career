package com.company.techblog.post;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import com.company.techblog.domain.Post;
import com.company.techblog.domain.PostLike;
import com.company.techblog.domain.User;
import com.company.techblog.dto.LikeResponse;
import com.company.techblog.dto.PostDto;
import com.company.techblog.repository.LikeRepository;
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

    @Mock
    private LikeRepository likeRepository;

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

    @DisplayName("게시글 좋아요 토글 - 좋아요 추가")
    @Test
    void toggleLike_WhenNotLiked_ShouldAddLike() {
        // given
        Long postId = 1L;
        Long userId = 2L;
        User user = User.builder().build();
        TestHelper.setId(user, userId);

        User author = User.builder().build();
        TestHelper.setId(author, 3L);

        Post post = Post.builder()
            .author(author)
            .title("Test Post")
            .content("Test Content")
            .build();
        TestHelper.setId(post, postId);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(likeRepository.findByPostIdAndUserId(postId, userId))
            .willReturn(Optional.empty());

        // when
        LikeResponse response = postService.toggleLike(postId, userId);

        // then
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.isLiked()).isTrue();
        assertThat(response.getLikeCount()).isEqualTo(1L);

        verify(likeRepository).save(any(PostLike.class));
    }

    @DisplayName("게시글 좋아요 토글 - 좋아요 취소")
    @Test
    void toggleLike_WhenAlreadyLiked_ShouldRemoveLike() {
        // given
        Long postId = 1L;
        Long userId = 2L;
        User user = User.builder().build();
        TestHelper.setId(user, userId);

        User author = User.builder().build();
        TestHelper.setId(author, 3L);

        Post post = Post.builder()
            .author(author)
            .title("Test Post")
            .content("Test Content")
            .build();
        TestHelper.setId(post, postId);

        post.increaseLikeCount();

        PostLike existingLike = PostLike.builder()
            .post(post)
            .user(user)
            .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(likeRepository.findByPostIdAndUserId(postId, userId))
            .willReturn(Optional.of(existingLike));

        // when
        LikeResponse response = postService.toggleLike(postId, userId);

        // then
        assertThat(response.getPostId()).isEqualTo(postId);
        assertThat(response.isLiked()).isFalse();
        assertThat(response.getLikeCount()).isEqualTo(0L);

        verify(likeRepository).delete(existingLike);
    }

    @DisplayName("본인 게시글 좋아요 시도 시 예외 발생")
    @Test
    void toggleLike_WhenLikingOwnPost_ShouldThrowException() {
        // given
        Long userId = 1L;
        User user = User.builder().build();
        TestHelper.setId(user, userId);

        Post post = Post.builder()
            .author(user)
            .title("Test Post")
            .content("Test Content")
            .build();
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> postService.toggleLike(1L, userId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot like your own post");

        verify(likeRepository, never()).save(any());
        verify(likeRepository, never()).delete(any());
    }
}
package com.company.techblog.service;

import com.company.techblog.domain.Post;
import com.company.techblog.domain.PostLike;
import com.company.techblog.domain.User;
import com.company.techblog.domain.UserStatus;
import com.company.techblog.dto.LikeResponse;
import com.company.techblog.dto.PostDto;
import com.company.techblog.dto.PostDto.Response;
import com.company.techblog.repository.LikeRepository;
import com.company.techblog.repository.PostRepository;
import com.company.techblog.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public PostDto.Response createPost(PostDto.Request request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ADD THIS CODE
        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new IllegalStateException("Inactive user cannot create posts");
        }

        Post post = Post.builder()
            .author(user)
            .title(request.getTitle())
            .content(request.getContent())
            .build();

        return PostDto.Response.from(postRepository.save(post));
    }

    public PostDto.Response getPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        return PostDto.Response.from(post);
    }

    public List<Response> getAllPosts() {
        return postRepository.findAll().stream()
            .map(PostDto.Response::from)
            .collect(Collectors.toList());
    }

    public List<Response> getPopularPosts() {
        return postRepository.findTop10ByOrderByLikeCountDescCreatedAtDesc().stream()
            .map(PostDto.Response::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public PostDto.Response updatePost(Long postId, PostDto.Request request) {
        // // ADD THIS CODE
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new IllegalStateException("Inactive user cannot update posts");
        }

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.isAuthor(request.getUserId())) {
            throw new IllegalStateException("Only author can update post");
        }

        post.update(request.getTitle(), request.getContent());
        return PostDto.Response.from(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.isAuthor(userId)) {
            throw new IllegalStateException("Only author can delete post");
        }

        postRepository.delete(post);
    }

    @Transactional
    public LikeResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (post.isAuthor(userId)) {
            throw new IllegalStateException("Cannot like your own post");
        }

        Optional<PostLike> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);

        boolean liked;
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.decreaseLikeCount();
            liked = false;
        } else {
            PostLike postLike = PostLike.builder()
                .post(post)
                .user(user)
                .build();
            likeRepository.save(postLike);
            post.increaseLikeCount();
            liked = true;
        }

        return LikeResponse.builder()
            .postId(postId)
            .likeCount(post.getLikeCount())
            .liked(liked)
            .build();
    }


}
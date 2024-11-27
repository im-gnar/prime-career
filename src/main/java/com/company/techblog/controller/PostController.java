package com.company.techblog.controller;

import com.company.techblog.dto.LikeRequest;
import com.company.techblog.dto.LikeResponse;
import com.company.techblog.dto.PostDto;
import com.company.techblog.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
        @Valid @RequestBody PostDto.Request request) {
        return ResponseEntity.ok(postService.createPost(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto.Response> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping
    public ResponseEntity<List<PostDto.Response>> getAllPosts(@RequestParam(required = false, defaultValue = "false") Boolean popular) {
        if (Boolean.TRUE.equals(popular)) {
            return ResponseEntity.ok(postService.getPopularPosts());
        }
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto.Response> updatePost(
        @PathVariable Long id,
        @Valid @RequestBody PostDto.Request request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestParam Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<LikeResponse> toggleLike(@PathVariable Long postId, @Valid @RequestBody LikeRequest request) {
        return ResponseEntity.ok(postService.toggleLike(postId, request.getUserId()));
    }

}
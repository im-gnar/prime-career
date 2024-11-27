package com.company.techblog.controller;

import com.company.techblog.dto.PostDto;
import com.company.techblog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public ResponseEntity<List<PostDto.Response>> getAllPosts() {
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
}
package com.company.techblog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import com.company.techblog.domain.Post;
import java.time.LocalDateTime;
import lombok.Setter;

public class PostDto {

    @Getter
    @Setter
    public static class Request {

        @NotNull
        private Long userId;

        @NotBlank(message = "제목은 필수입니다")
        @Size(min = 1, max = 100, message = "제목은 1-100자 사이여야 합니다")
        private String title;

        @NotBlank(message = "내용은 필수입니다")
        private String content;

    }

    @Getter
    @Builder
    public static class Response {
        private final Long id;
        private final String title;
        private final String content;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static Response from(Post post) {
            return Response.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
        }
    }
}
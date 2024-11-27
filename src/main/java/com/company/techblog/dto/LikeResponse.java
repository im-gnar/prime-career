package com.company.techblog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {
    private Long postId;
    private Long likeCount;
    private boolean liked;
}
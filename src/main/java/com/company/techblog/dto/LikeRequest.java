package com.company.techblog.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LikeRequest {
    @NotNull
    private Long userId;
}
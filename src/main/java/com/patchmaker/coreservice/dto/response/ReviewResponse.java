package com.patchmaker.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long developerId;
    private String developerName;
    private Long projectId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
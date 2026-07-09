package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.request.ReviewRequest;
import com.patchmaker.coreservice.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long projectId, ReviewRequest request);
    List<ReviewResponse> getProjectReviews(Long projectId);
}
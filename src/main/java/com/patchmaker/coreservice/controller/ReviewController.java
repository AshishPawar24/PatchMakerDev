package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.request.ReviewRequest;
import com.patchmaker.coreservice.dto.response.ReviewResponse;
import com.patchmaker.coreservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/projects/{projectId}")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long projectId,
                                                       @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(projectId, request));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ReviewResponse>> getProjectReviews(@PathVariable Long projectId) {
        return ResponseEntity.ok(reviewService.getProjectReviews(projectId));
    }
}
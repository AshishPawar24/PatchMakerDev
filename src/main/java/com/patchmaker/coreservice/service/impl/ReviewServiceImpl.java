package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.request.ReviewRequest;
import com.patchmaker.coreservice.dto.response.ReviewResponse;
import com.patchmaker.coreservice.entity.ApplicationStatus;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.Review;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.exception.DuplicateReviewException;
import com.patchmaker.coreservice.exception.ResourceNotFoundException;
import com.patchmaker.coreservice.exception.ReviewNotAllowedException;
import com.patchmaker.coreservice.repository.ApplicationRepository;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.repository.ReviewRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public ReviewResponse createReview(Long projectId, ReviewRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        boolean hasAcceptedApplication = applicationRepository
                .existsByDeveloperIdAndProjectIdAndStatus(currentUser.getId(), projectId, ApplicationStatus.ACCEPTED);

        if (!hasAcceptedApplication) {
            throw new ReviewNotAllowedException("Only accepted contributors can review this project");
        }

        if (reviewRepository.existsByDeveloperIdAndProjectId(currentUser.getId(), projectId)) {
            throw new DuplicateReviewException("You have already reviewed this project");
        }

        Review review = Review.builder()
                .developer(currentUser)
                .project(project)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    public List<ReviewResponse> getProjectReviews(Long projectId) {
        return reviewRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .developerId(review.getDeveloper().getId())
                .developerName(review.getDeveloper().getName())
                .projectId(review.getProject().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.response.ProjectRecommendationResponse;
import com.patchmaker.coreservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/projects")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<List<ProjectRecommendationResponse>> getRecommendations() {
        return ResponseEntity.ok(recommendationService.getRecommendations());
    }
}
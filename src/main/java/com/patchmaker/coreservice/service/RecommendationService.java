package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.response.ProjectRecommendationResponse;

import java.util.List;

public interface RecommendationService {
    List<ProjectRecommendationResponse> getRecommendations();
}
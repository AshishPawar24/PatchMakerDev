package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.response.PagedResponse;
import com.patchmaker.coreservice.dto.response.ProjectResponse;
import com.patchmaker.coreservice.entity.DifficultyLevel;
import com.patchmaker.coreservice.entity.ProjectStatus;

public interface SearchService {
    PagedResponse<ProjectResponse> searchProjects(String keyword, String technology,
                                                  DifficultyLevel difficultyLevel, String role,
                                                  ProjectStatus status, String sortBy,
                                                  int page, int size);
}
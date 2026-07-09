package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.request.ProjectRequest;
import com.patchmaker.coreservice.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(ProjectRequest request);
    ProjectResponse updateProject(Long projectId, ProjectRequest request);
    void closeProject(Long projectId);
    List<ProjectResponse> getMyProjects();
    List<ProjectResponse> getAllOpenProjects();
}
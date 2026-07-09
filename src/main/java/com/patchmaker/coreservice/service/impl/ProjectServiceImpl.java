package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.request.ProjectRequest;
import com.patchmaker.coreservice.dto.response.ProjectResponse;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.ProjectStatus;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.exception.ResourceNotFoundException;
import com.patchmaker.coreservice.exception.UnauthorizedActionException;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        User currentUser = getCurrentUser();

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .githubRepoUrl(request.getGithubRepoUrl())
                .techStack(request.getTechStack())
                .requiredRoles(request.getRequiredRoles())
                .difficultyLevel(request.getDifficultyLevel())
                .status(ProjectStatus.OPEN)
                .maintainer(currentUser)
                .build();

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Project project = getProjectOrThrow(projectId);
        verifyOwnership(project);

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setGithubRepoUrl(request.getGithubRepoUrl());
        project.setTechStack(request.getTechStack());
        project.setRequiredRoles(request.getRequiredRoles());
        project.setDifficultyLevel(request.getDifficultyLevel());

        Project updated = projectRepository.save(project);
        return mapToResponse(updated);
    }

    @Override
    public void closeProject(Long projectId) {
        Project project = getProjectOrThrow(projectId);
        verifyOwnership(project);

        project.setStatus(ProjectStatus.CLOSED);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectResponse> getMyProjects() {
        User currentUser = getCurrentUser();
        return projectRepository.findByMaintainerId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProjectResponse> getAllOpenProjects() {
        return projectRepository.findByStatus(ProjectStatus.OPEN)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }

    private void verifyOwnership(Project project) {
        User currentUser = getCurrentUser();
        if (!project.getMaintainer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You do not have permission to modify this project");
        }
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .githubRepoUrl(project.getGithubRepoUrl())
                .techStack(project.getTechStack())
                .requiredRoles(project.getRequiredRoles())
                .difficultyLevel(project.getDifficultyLevel())
                .status(project.getStatus())
                .maintainerId(project.getMaintainer().getId())
                .maintainerName(project.getMaintainer().getName())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
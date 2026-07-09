package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.request.ApplicationRequest;
import com.patchmaker.coreservice.dto.request.ApplicationStatusUpdateRequest;
import com.patchmaker.coreservice.dto.response.ApplicationResponse;
import com.patchmaker.coreservice.entity.*;
import com.patchmaker.coreservice.exception.*;
import com.patchmaker.coreservice.repository.ApplicationRepository;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ApplicationResponse applyToProject(ApplicationRequest request) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));

        if (project.getStatus() != ProjectStatus.OPEN) {
            throw new ProjectClosedException("This project is not open for applications");
        }

        if (applicationRepository.existsByDeveloperIdAndProjectId(currentUser.getId(), project.getId())) {
            throw new DuplicateApplicationException("You have already applied to this project");
        }

        Application application = Application.builder()
                .developer(currentUser)
                .project(project)
                .status(ApplicationStatus.PENDING)
                .message(request.getMessage())
                .build();

        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Override
    public List<ApplicationResponse> getMyApplications() {
        User currentUser = getCurrentUser();
        return applicationRepository.findByDeveloperId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ApplicationResponse> getApplicationsForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        verifyProjectOwnership(project);

        return applicationRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

        verifyProjectOwnership(application.getProject());

        application.setStatus(request.getStatus());
        Application updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    private void verifyProjectOwnership(Project project) {
        User currentUser = getCurrentUser();
        if (!project.getMaintainer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You do not have permission to access applications for this project");
        }
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .applicationId(application.getId())
                .developerId(application.getDeveloper().getId())
                .developerName(application.getDeveloper().getName())
                .projectId(application.getProject().getId())
                .projectTitle(application.getProject().getTitle())
                .status(application.getStatus())
                .message(application.getMessage())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
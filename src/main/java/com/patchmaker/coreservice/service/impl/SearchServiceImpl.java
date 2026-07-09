package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.response.PagedResponse;
import com.patchmaker.coreservice.dto.response.ProjectResponse;
import com.patchmaker.coreservice.entity.DifficultyLevel;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.ProjectStatus;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.repository.specification.ProjectSpecification;
import com.patchmaker.coreservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProjectRepository projectRepository;

    @Override
    public PagedResponse<ProjectResponse> searchProjects(String keyword, String technology,
                                                         DifficultyLevel difficultyLevel, String role,
                                                         ProjectStatus status, String sortBy,
                                                         int page, int size) {

        int safePage = Math.max(page, 0);
        int safeSize = (size <= 0 || size > MAX_PAGE_SIZE) ? 10 : size;

        Specification<Project> spec = ProjectSpecification.withFilters(keyword, technology, difficultyLevel, role, status);

        Pageable pageable;
        if ("difficulty".equalsIgnoreCase(sortBy)) {
            spec = spec.and(ProjectSpecification.orderByDifficulty());
            pageable = PageRequest.of(safePage, safeSize);
        } else {
            // "newest" (default) and "relevance" both fall back to recency ordering —
            // true relevance ranking would need full-text search scoring (e.g. Elasticsearch), a future upgrade
            pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        }

        Page<Project> resultPage = projectRepository.findAll(spec, pageable);

        return new PagedResponse<>(
                resultPage.getContent().stream().map(this::mapToResponse).toList(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );
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
package com.patchmaker.coreservice.dto.response;

import com.patchmaker.coreservice.entity.DifficultyLevel;
import com.patchmaker.coreservice.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private String githubRepoUrl;
    private List<String> techStack;
    private List<String> requiredRoles;
    private DifficultyLevel difficultyLevel;
    private ProjectStatus status;
    private Long maintainerId;
    private String maintainerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
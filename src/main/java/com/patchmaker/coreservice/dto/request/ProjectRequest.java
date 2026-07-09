package com.patchmaker.coreservice.dto.request;

import com.patchmaker.coreservice.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String githubRepoUrl;

    @NotEmpty(message = "At least one technology is required")
    private List<String> techStack;

    @NotEmpty(message = "At least one required role is needed")
    private List<String> requiredRoles;

    @NotNull(message = "Difficulty level is required")
    private DifficultyLevel difficultyLevel;
}
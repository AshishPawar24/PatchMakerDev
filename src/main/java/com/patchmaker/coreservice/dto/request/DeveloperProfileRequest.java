package com.patchmaker.coreservice.dto.request;

import com.patchmaker.coreservice.entity.ExperienceLevel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DeveloperProfileRequest {

    private String bio;

    private String githubUrl;

    @NotEmpty(message = "At least one skill is required")
    private List<String> skills;

    @NotEmpty(message = "At least one language is required")
    private List<String> languages;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    private List<String> interests;
}
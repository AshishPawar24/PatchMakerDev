package com.patchmaker.coreservice.dto.response;

import com.patchmaker.coreservice.entity.ExperienceLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DeveloperProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String bio;
    private String githubUrl;
    private List<String> skills;
    private List<String> languages;
    private ExperienceLevel experienceLevel;
    private List<String> interests;
}
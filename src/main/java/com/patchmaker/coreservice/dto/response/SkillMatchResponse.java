package com.patchmaker.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SkillMatchResponse {
    private Long projectId;
    private String projectTitle;
    private Integer matchScore;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private String summary;
}
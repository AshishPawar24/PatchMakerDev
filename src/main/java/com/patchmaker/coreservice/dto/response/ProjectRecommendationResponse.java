package com.patchmaker.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProjectRecommendationResponse {
    private Long projectId;
    private String projectName;
    private Integer matchScore;
    private List<String> matchedSkills;
}
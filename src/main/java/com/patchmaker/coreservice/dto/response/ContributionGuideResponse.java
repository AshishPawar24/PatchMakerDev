package com.patchmaker.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ContributionGuideResponse {
    private Long projectId;
    private String projectTitle;
    private List<String> steps;
}
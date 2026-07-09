package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.response.ContributionGuideResponse;
import com.patchmaker.coreservice.dto.response.SkillMatchResponse;

public interface AIService {
    SkillMatchResponse analyzeSkillMatch(Long projectId);
    ContributionGuideResponse generateContributionGuide(Long projectId);
}
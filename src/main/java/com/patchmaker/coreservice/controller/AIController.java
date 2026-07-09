package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.response.ContributionGuideResponse;
import com.patchmaker.coreservice.dto.response.SkillMatchResponse;
import com.patchmaker.coreservice.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/projects/{projectId}/match")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<SkillMatchResponse> matchSkills(@PathVariable Long projectId) {
        return ResponseEntity.ok(aiService.analyzeSkillMatch(projectId));
    }

    @GetMapping("/projects/{projectId}/guide")
    public ResponseEntity<ContributionGuideResponse> getContributionGuide(@PathVariable Long projectId) {
        return ResponseEntity.ok(aiService.generateContributionGuide(projectId));
    }
}
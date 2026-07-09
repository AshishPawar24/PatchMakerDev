package com.patchmaker.coreservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.patchmaker.coreservice.client.GroqClient;
import com.patchmaker.coreservice.dto.response.ContributionGuideResponse;
import com.patchmaker.coreservice.dto.response.SkillMatchResponse;
import com.patchmaker.coreservice.entity.DeveloperProfile;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.exception.AIServiceException;
import com.patchmaker.coreservice.exception.ResourceNotFoundException;
import com.patchmaker.coreservice.repository.DeveloperProfileRepository;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.AIService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final GroqClient groqClient;
    private final ProjectRepository projectRepository;
    private final DeveloperProfileRepository developerProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SkillMatchResponse analyzeSkillMatch(Long projectId) {
        User currentUser = getCurrentUser();

        DeveloperProfile profile = developerProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Create a developer profile before requesting skill match"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        String prompt = buildSkillMatchPrompt(profile, project);
        String aiResponse = groqClient.getCompletion(prompt);

        AiSkillMatchResult result = parseJson(aiResponse, AiSkillMatchResult.class, "skill match");

        return SkillMatchResponse.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .matchScore(result.getMatchScore())
                .matchingSkills(result.getMatchingSkills())
                .missingSkills(result.getMissingSkills())
                .summary(result.getSummary())
                .build();
    }

    @Override
    public ContributionGuideResponse generateContributionGuide(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        String prompt = buildContributionGuidePrompt(project);
        String aiResponse = groqClient.getCompletion(prompt);

        AiGuideResult result = parseJson(aiResponse, AiGuideResult.class, "contribution guide");

        return ContributionGuideResponse.builder()
                .projectId(project.getId())
                .projectTitle(project.getTitle())
                .steps(result.getSteps())
                .build();
    }

    private String buildSkillMatchPrompt(DeveloperProfile profile, Project project) {
        return """
                You are an open-source mentor helping a developer find suitable projects.

                Developer skills: %s
                Developer experience level: %s

                Project title: %s
                Project required tech stack: %s
                Project difficulty level: %s

                Analyze the compatibility and respond with ONLY valid JSON, no markdown, no extra text, in this exact format:
                {
                  "matchScore": <integer 0-100>,
                  "matchingSkills": ["skill1", "skill2"],
                  "missingSkills": ["skill1", "skill2"],
                  "summary": "one or two sentence practical advice"
                }
                """.formatted(
                String.join(", ", profile.getSkills()),
                profile.getExperienceLevel(),
                project.getTitle(),
                String.join(", ", project.getTechStack()),
                project.getDifficultyLevel()
        );
    }

    private String buildContributionGuidePrompt(Project project) {
        return """
                You are an open-source mentor creating a beginner contribution roadmap.

                Project title: %s
                Description: %s
                Tech stack: %s
                Difficulty level: %s

                Create a step-by-step contribution roadmap for a new contributor.
                Respond with ONLY valid JSON, no markdown, no extra text, in this exact format:
                {
                  "steps": ["step 1", "step 2", "step 3"]
                }
                """.formatted(
                project.getTitle(),
                project.getDescription(),
                String.join(", ", project.getTechStack()),
                project.getDifficultyLevel()
        );
    }

    private <T> T parseJson(String rawResponse, Class<T> targetClass, String context) {
        String cleaned = rawResponse.replace("```json", "").replace("```", "").trim();
        try {
            return objectMapper.readValue(cleaned, targetClass);
        } catch (JsonProcessingException ex) {
            throw new AIServiceException("Failed to parse AI-generated " + context + " response");
        }
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }

    @Data
    @NoArgsConstructor
    private static class AiSkillMatchResult {
        private Integer matchScore;
        private List<String> matchingSkills;
        private List<String> missingSkills;
        private String summary;
    }

    @Data
    @NoArgsConstructor
    private static class AiGuideResult {
        private List<String> steps;
    }
}
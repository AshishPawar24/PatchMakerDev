package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.response.ProjectRecommendationResponse;
import com.patchmaker.coreservice.entity.DeveloperProfile;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.ProjectStatus;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.exception.ResourceNotFoundException;
import com.patchmaker.coreservice.repository.DeveloperProfileRepository;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final DeveloperProfileRepository developerProfileRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<ProjectRecommendationResponse> getRecommendations() {
        User currentUser = getCurrentUser();

        DeveloperProfile profile = developerProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Create a developer profile before requesting recommendations"));

        List<String> developerSkills = profile.getSkills();

        return projectRepository.findByStatus(ProjectStatus.OPEN).stream()
                .map(project -> buildRecommendation(project, developerSkills))
                .sorted(Comparator.comparingInt(ProjectRecommendationResponse::getMatchScore).reversed())
                .toList();
    }

    private ProjectRecommendationResponse buildRecommendation(Project project, List<String> developerSkills) {
        List<String> techStack = project.getTechStack();

        List<String> matchedSkills = techStack.stream()
                .filter(tech -> developerSkills.stream().anyMatch(skill -> skill.equalsIgnoreCase(tech)))
                .toList();

        int matchScore = techStack.isEmpty()
                ? 0
                : (int) Math.round((matchedSkills.size() * 100.0) / techStack.size());

        return ProjectRecommendationResponse.builder()
                .projectId(project.getId())
                .projectName(project.getTitle())
                .matchScore(matchScore)
                .matchedSkills(matchedSkills)
                .build();
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }
}
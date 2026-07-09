package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.request.DeveloperProfileRequest;
import com.patchmaker.coreservice.dto.response.DeveloperProfileResponse;
import com.patchmaker.coreservice.entity.DeveloperProfile;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.exception.ProfileAlreadyExistsException;
import com.patchmaker.coreservice.exception.ResourceNotFoundException;
import com.patchmaker.coreservice.repository.DeveloperProfileRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.DeveloperProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeveloperProfileServiceImpl implements DeveloperProfileService {

    private final DeveloperProfileRepository developerProfileRepository;

    @Override
    public DeveloperProfileResponse createProfile(DeveloperProfileRequest request) {
        User currentUser = getCurrentUser();

        if (developerProfileRepository.existsByUserId(currentUser.getId())) {
            throw new ProfileAlreadyExistsException("Developer profile already exists for this user");
        }

//        MANUAL MAPPING :-We're doing manual mapping here, not MapStruct — with only one DTO pair it's simpler and clearer to read.

        DeveloperProfile profile = DeveloperProfile.builder()
                .user(currentUser)
                .bio(request.getBio())
                .githubUrl(request.getGithubUrl())
                .skills(request.getSkills())
                .languages(request.getLanguages())
                .experienceLevel(request.getExperienceLevel())
                .interests(request.getInterests())
                .build();

        DeveloperProfile saved = developerProfileRepository.save(profile);
        return mapToResponse(saved);
    }

    @Override
    public DeveloperProfileResponse getMyProfile() {
        User currentUser = getCurrentUser();
        DeveloperProfile profile = developerProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Developer profile not found"));
        return mapToResponse(profile);
    }

    @Override
    public DeveloperProfileResponse updateProfile(DeveloperProfileRequest request) {
        User currentUser = getCurrentUser();
        DeveloperProfile profile = developerProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Developer profile not found"));

        profile.setBio(request.getBio());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setSkills(request.getSkills());
        profile.setLanguages(request.getLanguages());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setInterests(request.getInterests());

        DeveloperProfile updated = developerProfileRepository.save(profile);
        return mapToResponse(updated);
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }

    private DeveloperProfileResponse mapToResponse(DeveloperProfile profile) {
        return DeveloperProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .name(profile.getUser().getName())
                .email(profile.getUser().getEmail())
                .bio(profile.getBio())
                .githubUrl(profile.getGithubUrl())
                .skills(profile.getSkills())
                .languages(profile.getLanguages())
                .experienceLevel(profile.getExperienceLevel())
                .interests(profile.getInterests())
                .build();
    }
}
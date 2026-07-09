package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.request.DeveloperProfileRequest;
import com.patchmaker.coreservice.dto.response.DeveloperProfileResponse;

public interface DeveloperProfileService {
    DeveloperProfileResponse createProfile(DeveloperProfileRequest request);
    DeveloperProfileResponse getMyProfile();
    DeveloperProfileResponse updateProfile(DeveloperProfileRequest request);
}
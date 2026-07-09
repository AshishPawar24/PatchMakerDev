package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.request.DeveloperProfileRequest;
import com.patchmaker.coreservice.dto.response.DeveloperProfileResponse;
import com.patchmaker.coreservice.service.DeveloperProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/developers/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DEVELOPER')")
public class DeveloperProfileController {

    private final DeveloperProfileService developerProfileService;

    @PostMapping
    public ResponseEntity<DeveloperProfileResponse> createProfile(@Valid @RequestBody DeveloperProfileRequest request) {
        return ResponseEntity.ok(developerProfileService.createProfile(request));
    }

    @GetMapping
    public ResponseEntity<DeveloperProfileResponse> getMyProfile() {
        return ResponseEntity.ok(developerProfileService.getMyProfile());
    }

    @PutMapping
    public ResponseEntity<DeveloperProfileResponse> updateProfile(@Valid @RequestBody DeveloperProfileRequest request) {
        return ResponseEntity.ok(developerProfileService.updateProfile(request));
    }
}
package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.request.ApplicationRequest;
import com.patchmaker.coreservice.dto.request.ApplicationStatusUpdateRequest;
import com.patchmaker.coreservice.dto.response.ApplicationResponse;
import com.patchmaker.coreservice.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.applyToProject(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications() {
        return ResponseEntity.ok(applicationService.getMyApplications());
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('MAINTAINER')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(applicationService.getApplicationsForProject(projectId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MAINTAINER')")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, request));
    }
}
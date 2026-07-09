package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.request.ProjectRequest;
import com.patchmaker.coreservice.dto.response.ProjectResponse;
import com.patchmaker.coreservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('MAINTAINER')")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MAINTAINER')")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MAINTAINER')")
    public ResponseEntity<Void> closeProject(@PathVariable Long id) {
        projectService.closeProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('MAINTAINER')")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllOpenProjects() {
        return ResponseEntity.ok(projectService.getAllOpenProjects());
    }
}
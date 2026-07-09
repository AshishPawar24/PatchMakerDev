package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.response.PagedResponse;
import com.patchmaker.coreservice.dto.response.ProjectResponse;
import com.patchmaker.coreservice.entity.DifficultyLevel;
import com.patchmaker.coreservice.entity.ProjectStatus;
import com.patchmaker.coreservice.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/projects")
    public ResponseEntity<PagedResponse<ProjectResponse>> searchProjects(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) DifficultyLevel difficulty,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                searchService.searchProjects(keyword, technology, difficulty, role, status, sortBy, page, size)
        );
    }
}
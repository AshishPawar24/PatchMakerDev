package com.patchmaker.coreservice.controller;

import com.patchmaker.coreservice.dto.response.BookmarkResponse;
import com.patchmaker.coreservice.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DEVELOPER')")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{projectId}")
    public ResponseEntity<BookmarkResponse> addBookmark(@PathVariable Long projectId) {
        return ResponseEntity.ok(bookmarkService.addBookmark(projectId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long projectId) {
        bookmarkService.removeBookmark(projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponse>> getMyBookmarks() {
        return ResponseEntity.ok(bookmarkService.getMyBookmarks());
    }
}
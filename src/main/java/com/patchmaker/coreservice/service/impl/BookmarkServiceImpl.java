package com.patchmaker.coreservice.service.impl;

import com.patchmaker.coreservice.dto.response.BookmarkResponse;
import com.patchmaker.coreservice.entity.Bookmark;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.User;
import com.patchmaker.coreservice.exception.DuplicateBookmarkException;
import com.patchmaker.coreservice.exception.ResourceNotFoundException;
import com.patchmaker.coreservice.repository.BookmarkRepository;
import com.patchmaker.coreservice.repository.ProjectRepository;
import com.patchmaker.coreservice.security.UserPrincipal;
import com.patchmaker.coreservice.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ProjectRepository projectRepository;

    @Override
    public BookmarkResponse addBookmark(Long projectId) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (bookmarkRepository.existsByDeveloperIdAndProjectId(currentUser.getId(), projectId)) {
            throw new DuplicateBookmarkException("You have already bookmarked this project");
        }

        Bookmark bookmark = Bookmark.builder()
                .developer(currentUser)
                .project(project)
                .build();

        Bookmark saved = bookmarkRepository.save(bookmark);
        return mapToResponse(saved);
    }

    @Override
    public void removeBookmark(Long projectId) {
        User currentUser = getCurrentUser();

        Bookmark bookmark = bookmarkRepository.findByDeveloperIdAndProjectId(currentUser.getId(), projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found for this project"));

        bookmarkRepository.delete(bookmark);
    }

    @Override
    public List<BookmarkResponse> getMyBookmarks() {
        User currentUser = getCurrentUser();
        return bookmarkRepository.findByDeveloperId(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUser();
    }

    private BookmarkResponse mapToResponse(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .bookmarkId(bookmark.getId())
                .projectId(bookmark.getProject().getId())
                .projectTitle(bookmark.getProject().getTitle())
                .projectDescription(bookmark.getProject().getDescription())
                .savedAt(bookmark.getCreatedAt())
                .build();
    }
}
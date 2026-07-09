package com.patchmaker.coreservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookmarkResponse {
    private Long bookmarkId;
    private Long projectId;
    private String projectTitle;
    private String projectDescription;
    private LocalDateTime savedAt;
}
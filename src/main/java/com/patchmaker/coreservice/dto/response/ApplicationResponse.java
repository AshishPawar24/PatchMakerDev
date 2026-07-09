package com.patchmaker.coreservice.dto.response;

import com.patchmaker.coreservice.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private Long developerId;
    private String developerName;
    private Long projectId;
    private String projectTitle;
    private ApplicationStatus status;
    private String message;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
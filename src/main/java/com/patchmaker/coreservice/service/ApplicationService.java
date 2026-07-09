package com.patchmaker.coreservice.service;

import com.patchmaker.coreservice.dto.request.ApplicationRequest;
import com.patchmaker.coreservice.dto.request.ApplicationStatusUpdateRequest;
import com.patchmaker.coreservice.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse applyToProject(ApplicationRequest request);
    List<ApplicationResponse> getMyApplications();
    List<ApplicationResponse> getApplicationsForProject(Long projectId);
    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusUpdateRequest request);
}
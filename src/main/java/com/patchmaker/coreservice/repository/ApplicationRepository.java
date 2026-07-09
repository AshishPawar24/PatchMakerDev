package com.patchmaker.coreservice.repository;

import com.patchmaker.coreservice.entity.Application;
import com.patchmaker.coreservice.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByDeveloperId(Long developerId);

    List<Application> findByProjectId(Long projectId);

    boolean existsByDeveloperIdAndProjectId(Long developerId, Long projectId);

    boolean existsByDeveloperIdAndProjectIdAndStatus(Long developerId, Long projectId, ApplicationStatus status);
}
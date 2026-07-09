package com.patchmaker.coreservice.repository;

import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    List<Project> findByMaintainerId(Long maintainerId);

    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p JOIN p.techStack t WHERE t = :technology AND p.status = 'OPEN'")
    List<Project> findOpenProjectsByTechnology(@Param("technology") String technology);
}
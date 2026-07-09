package com.patchmaker.coreservice.repository;

import com.patchmaker.coreservice.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    List<Bookmark> findByDeveloperId(Long developerId);

    Optional<Bookmark> findByDeveloperIdAndProjectId(Long developerId, Long projectId);

    boolean existsByDeveloperIdAndProjectId(Long developerId, Long projectId);
}
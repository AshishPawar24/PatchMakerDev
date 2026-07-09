package com.patchmaker.coreservice.repository;

import com.patchmaker.coreservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProjectId(Long projectId);

    boolean existsByDeveloperIdAndProjectId(Long developerId, Long projectId);
}
package com.patchmaker.coreservice.repository.specification;

import com.patchmaker.coreservice.entity.DifficultyLevel;
import com.patchmaker.coreservice.entity.Project;
import com.patchmaker.coreservice.entity.ProjectStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProjectSpecification {

    public static Specification<Project> withFilters(String keyword, String technology,
                                                     DifficultyLevel difficultyLevel,
                                                     String role, ProjectStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (technology != null && !technology.isBlank()) {
                query.distinct(true);
                predicates.add(cb.equal(cb.lower(root.join("techStack")), technology.toLowerCase()));
            }

            if (role != null && !role.isBlank()) {
                query.distinct(true);
                predicates.add(cb.equal(cb.lower(root.join("requiredRoles")), role.toLowerCase()));
            }

            if (difficultyLevel != null) {
                predicates.add(cb.equal(root.get("difficultyLevel"), difficultyLevel));
            }

            // Default to OPEN projects if status isn't specified — developers are searching to join, not to browse history
            predicates.add(cb.equal(root.get("status"), status != null ? status : ProjectStatus.OPEN));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Project> orderByDifficulty() {
        return (root, query, cb) -> {
            Expression<Integer> difficultyRank = cb.<Integer>selectCase()
                    .when(cb.equal(root.get("difficultyLevel"), DifficultyLevel.BEGINNER), 1)
                    .when(cb.equal(root.get("difficultyLevel"), DifficultyLevel.INTERMEDIATE), 2)
                    .when(cb.equal(root.get("difficultyLevel"), DifficultyLevel.ADVANCED), 3)
                    .otherwise(4);
            query.orderBy(cb.asc(difficultyRank));
            return cb.conjunction(); // always-true predicate — this specification only affects ordering
        };
    }
}
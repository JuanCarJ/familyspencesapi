package com.familyspencesapi.repositories.goal;

import com.familyspencesapi.domain.goals.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRepositoryGoal extends JpaRepository<Goal, UUID> {
    Optional<Goal> findByFamilyIdAndCategoryIdAndId(UUID familyId, UUID categoryId, UUID id);
    List<Goal> findByFamilyId(UUID familyId);
    Optional<Goal> findByFamilyIdAndId(UUID familyId, UUID id);
}

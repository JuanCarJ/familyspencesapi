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
    boolean existsByFamilyIdAndCategoryIdAndId(UUID familyId, UUID categoryId, UUID id);
    void deleteByFamilyIdAndCategoryIdAndId(UUID familyId, UUID categoryId, UUID id);

    List<Goal> findByFamilyId(UUID familyId);
    List<Goal> findByFamilyIdAndCategoryId(UUID familyId, UUID categoryId);
    Optional<Goal> findByFamilyIdAndId(UUID familyId, UUID id);
    boolean existsByFamilyIdAndId(UUID familyId, UUID id);
    void deleteByFamilyIdAndId(UUID familyId, UUID id);
}

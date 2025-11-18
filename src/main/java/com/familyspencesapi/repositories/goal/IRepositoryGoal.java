package com.familyspencesapi.repositories.goal;

import com.familyspencesapi.domain.goals.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRepositoryGoal extends JpaRepository<Goal, UUID> {
    List<Goal> findByCategoryId(UUID categoryId);
    Optional<Goal> findByCategoryIdAndId(UUID categoryId, UUID id);
    boolean existsByCategoryIdAndId(UUID categoryId, UUID id);
    void deleteByCategoryIdAndId(UUID categoryId, UUID id);
}

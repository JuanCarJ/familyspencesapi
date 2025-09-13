package com.familyspencesapi.repositories.goal;

import com.familyspencesapi.domain.goals.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IRepositoryGoal extends JpaRepository<Goal, UUID> {
}
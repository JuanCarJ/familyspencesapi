package com.familyspencesapi.service.goals;

import com.familyspencesapi.domain.goals.Goal;
import com.familyspencesapi.repositories.goal.IRepositoryGoal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoalService {

    private final IRepositoryGoal repository;

    public GoalService(IRepositoryGoal repository) {
        this.repository = repository;
    }

    public List<Goal> getAllGoals() {
        return repository.findAll();
    }

    public Optional<Goal> getGoalById(UUID id) {
        return repository.findById(id);
    }

    public Goal createGoal(Goal goal) {
        return repository.save(goal);
    }

    public Optional<Goal> updateGoal(UUID id, Goal goalDetails) {
        return repository.findById(id).map(goal -> {
            goal.setName(goalDetails.getName());
            goal.setDescription(goalDetails.getDescription());
            goal.setSavingsCap(goalDetails.getSavingsCap());
            goal.setDeadline(goalDetails.getDeadline());
            goal.setDailyGoal(goalDetails.getDailyGoal());
            // goal.setCategory(goalDetails.getCategory());  // Si activas la relación
            return repository.save(goal);
        });
    }

    public boolean deleteGoal(UUID id) {
        return repository.findById(id).map(goal -> {
            repository.delete(goal);
            return true;
        }).orElse(false);
    }
}

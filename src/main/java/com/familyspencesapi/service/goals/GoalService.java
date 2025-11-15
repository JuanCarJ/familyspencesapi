package com.familyspencesapi.service.goals;

import com.familyspencesapi.domain.goals.Goal;
import com.familyspencesapi.repositories.goal.IRepositoryGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoalService {

    private static final Logger log = LoggerFactory.getLogger(GoalService.class);
    private final IRepositoryGoal repository;

    public GoalService(IRepositoryGoal repository) {
        this.repository = repository;
    }

    public List<Goal> getAllGoals(UUID categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    public Goal getGoal(UUID categoryId, UUID goalId) {
        return repository.findByCategoryIdAndId(categoryId, goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found for category: " + categoryId + " and goal: " + goalId));
    }

    public Goal createGoal(Goal goal) {
        return repository.save(goal);
    }

    public Goal updateGoal(UUID goalId, Goal goalDetails) {
        Goal existing = repository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId));

        existing.setName(goalDetails.getName());
        existing.setDescription(goalDetails.getDescription());
        existing.setCategoryId(goalDetails.getCategoryId());
        existing.setSavingsCap(goalDetails.getSavingsCap());
        existing.setDeadline(goalDetails.getDeadline());
        existing.setDailyGoal(goalDetails.getDailyGoal());

        return repository.save(existing);
    }

    public void deleteGoal(UUID categoryId, UUID goalId) {
        if (!repository.existsByCategoryIdAndId(categoryId, goalId)) {
            throw new IllegalArgumentException("Goal not found for category: " + categoryId + " and goal: " + goalId);
        }
        repository.deleteByCategoryIdAndId(categoryId, goalId);
    }

    @Transactional
    public void saveFromProducer(Goal goal) {
        log.info("Saving goal from producer: {}", goal);
        repository.save(goal);
    }

    @Transactional
    public void updateFromProducer(Goal updatedGoal) {
        try {
            UUID goalId = updatedGoal.getId();
            UUID categoryId = updatedGoal.getCategoryId();

            if (goalId == null || categoryId == null) {
                log.warn("Missing categoryId or id in update event: {}", updatedGoal);
                return;
            }

            Optional<Goal> existingOpt = repository.findByCategoryIdAndId(categoryId, goalId);
            if (existingOpt.isEmpty()) {
                log.warn("Goal not found for update. Category: {}, Goal: {}", categoryId, goalId);
                return;
            }

            Goal existing = existingOpt.get();
            existing.setName(updatedGoal.getName());
            existing.setDescription(updatedGoal.getDescription());
            existing.setCategoryId(updatedGoal.getCategoryId());
            existing.setSavingsCap(updatedGoal.getSavingsCap());
            existing.setDeadline(updatedGoal.getDeadline());
            existing.setDailyGoal(updatedGoal.getDailyGoal());

            repository.save(existing);
            log.info("Goal updated successfully: {} for category {}", goalId, categoryId);

        } catch (Exception e) {
            log.error("Error processing Goal UPDATE event: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteFromProducer(Map<String, String> data) {
        try {
            String categoryStr = data.get("categoryId");
            String goalStr = data.get("goalId");

            if (categoryStr == null || goalStr == null) {
                log.warn("Missing fields in DELETE event: {}", data);
                return;
            }

            UUID categoryId = UUID.fromString(categoryStr);
            UUID goalId = UUID.fromString(goalStr);

            if (repository.existsByCategoryIdAndId(categoryId, goalId)) {
                repository.deleteByCategoryIdAndId(categoryId, goalId);
                log.info("Goal deleted successfully: {} for category {}", goalId, categoryId);
            } else {
                log.warn("Goal with id {} not found for category {}", goalId, categoryId);
            }

        } catch (Exception e) {
            log.error("Error deleting goal from producer event: {}", e.getMessage(), e);
        }
    }
}
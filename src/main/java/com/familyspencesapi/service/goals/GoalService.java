package com.familyspencesapi.service.goals;

import com.familyspencesapi.domain.goals.Goal;
import com.familyspencesapi.messages.goals.GoalsMessageSender;
import com.familyspencesapi.repositories.goal.IRepositoryGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GoalService {

    private static final Logger log = LoggerFactory.getLogger(GoalService.class);

    private final IRepositoryGoal repository;
    private final GoalsMessageSender goalMessageSender;

    public GoalService(IRepositoryGoal repository, GoalsMessageSender goalMessageSender) {
        this.repository = repository;
        this.goalMessageSender = goalMessageSender;
    }

    public List<Goal> getAllGoals(UUID familyId) {
        return repository.findByFamilyId(familyId);
    }

    public List<Goal> getAllGoalsAny() {
        return repository.findAll();
    }

    public Goal getGoal(UUID familyId, UUID goalId) {
        return repository.findByFamilyIdAndId(familyId, goalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Goal not found for family: " + familyId + " and goal: " + goalId
                ));
    }


    @Transactional
    public Goal createGoal(UUID familyId,UUID categoryId, Goal goal) {

        goal.setFamilyId(familyId);
        goal.setCategoryId(categoryId);

        Goal created = repository.save(goal);

        try {
            goalMessageSender.sendGoalCreated(created);
        } catch (Exception e) {
            log.error("Error sending GOAL CREATED event for {}: {}", created.getId(), e.getMessage(), e);
        }
        return created;
    }

    @Transactional
    public Goal updateGoal(UUID familyId,UUID goalId, Goal goalDetails, UUID categoryId) {
        Goal existing = repository.findByFamilyIdAndId(familyId, goalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Goal not found for family: " + familyId +
                                ", category: " + categoryId +
                                ", goal: " + goalId
                ));

        existing.setName(goalDetails.getName());
        existing.setDescription(goalDetails.getDescription());
        existing.setSavingsCap(goalDetails.getSavingsCap());
        existing.setDeadline(goalDetails.getDeadline());
        existing.setDailyGoal(goalDetails.getDailyGoal());
        existing.setCategoryId(goalDetails.getCategoryId());
        existing.setFamilyId(familyId);

        Goal updated = repository.save(existing);

        try {
            goalMessageSender.sendGoalUpdated(updated);
        } catch (Exception e) {
            log.error("Error sending GOAL UPDATED event for {}: {}", updated.getId(), e.getMessage(), e);
        }

        return updated;
    }

    @Transactional
    public void deleteGoal(UUID familyId, UUID goalId) {

        Goal existing = repository.findByFamilyIdAndId(familyId, goalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Goal not found for family: " + familyId + " and goal: " + goalId));

        try {
            repository.delete(existing);
        } catch (DataIntegrityViolationException ex) {
            log.error("Error deleting goal {}: {}", goalId, ex.getMessage(), ex);
            throw new IllegalStateException(
                    "La meta no se puede eliminar porque tiene registros asociados."
            );
        }

        try {
            goalMessageSender.sendGoalDeleted(
                    Map.of(
                            "familyId", familyId.toString(),
                            "goalId", goalId.toString()
                    )
            );
        } catch (Exception e) {
            log.error("Error sending GOAL DELETED event for {}: {}", goalId, e.getMessage(), e);
        }
    }
}


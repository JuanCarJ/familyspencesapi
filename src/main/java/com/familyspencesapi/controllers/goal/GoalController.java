package com.familyspencesapi.controllers.goal;

import com.familyspencesapi.domain.goals.Goal;
import com.familyspencesapi.messages.goals.GoalsMessageSender;
import com.familyspencesapi.service.goals.GoalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    private final GoalsMessageSender goalMessageSender;
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_KEY = "error";

    public GoalController(GoalService goalService, GoalsMessageSender goalMessageSender) {
        this.goalService = goalService;
        this.goalMessageSender = goalMessageSender;
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals(@RequestParam UUID categoryId) {
        List<Goal> goals = goalService.getAllGoals(categoryId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<Goal> getGoal(
            @RequestParam UUID categoryId,
            @PathVariable UUID goalId
    ) {
        Goal goal = goalService.getGoal(categoryId, goalId);
        return ResponseEntity.ok(goal);
    }

    @PostMapping
    public ResponseEntity<Object> createGoal(
            @RequestParam UUID categoryId,
            @RequestBody Goal goal
    ) {
        try {
            goal.setCategoryId(categoryId);

            Goal createdGoal = goalService.createGoal(goal);

            goalMessageSender.sendGoalCreated(createdGoal);

            return ResponseEntity.ok(createdGoal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Object> updateGoal(
            @RequestParam UUID categoryId,
            @PathVariable UUID goalId,
            @RequestBody Goal goal
    ) {
        try {
            goal.setCategoryId(categoryId);

            Goal updatedGoal = goalService.updateGoal(goalId, goal);

            goalMessageSender.sendGoalUpdated(updatedGoal);

            return ResponseEntity.ok(updatedGoal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Object> deleteGoal(
            @RequestParam UUID categoryId,
            @PathVariable UUID goalId
    ) {
        try {
            goalService.deleteGoal(categoryId, goalId);

            goalMessageSender.sendGoalDeleted(
                    Map.of("categoryId", categoryId.toString(), "goalId", goalId.toString())
            );

            return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }
}
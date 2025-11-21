package com.familyspencesapi.controllers.goal;

import com.familyspencesapi.domain.goals.Goal;
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
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_KEY = "error";

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals(@RequestParam UUID familyId) {
        List<Goal> goals = goalService.getAllGoals(familyId);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/any")
    public ResponseEntity<List<Goal>> getAllGoalsAny() {
        List<Goal> goals = goalService.getAllGoalsAny();
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<Goal> getGoal(
            @PathVariable UUID goalId,
            @RequestParam UUID familyId) {
        Goal goal = goalService.getGoal(familyId,goalId);
        return ResponseEntity.ok(goal);
    }

    @PostMapping
    public ResponseEntity<Object> createGoal(
            @RequestParam UUID familyId,
            @RequestParam UUID categoryId,
            @RequestBody Goal goal
    ) {
        try {
            Goal createdGoal = goalService.createGoal(familyId, categoryId, goal);
            return ResponseEntity.ok(createdGoal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Object> updateGoal(
            @RequestParam UUID familyId,
            @RequestParam UUID categoryId,
            @PathVariable UUID goalId,
            @RequestBody Goal goal
    ) {
        try {
            Goal updatedGoal = goalService.updateGoal(familyId,goalId, goal, categoryId);
            return ResponseEntity.ok(updatedGoal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Object> deleteGoal(
            @RequestParam UUID familyId,
            @PathVariable UUID goalId
    ) {
        try {
            goalService.deleteGoal(familyId, goalId);
            return ResponseEntity.ok(Map.of("message", "Goal deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }
}

package com.familyspencesapi.controllers.goal;

import com.familyspencesapi.domain.goals.Goal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {


    private final Map<UUID, Goal> goals = new HashMap<>();


    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        return ResponseEntity.ok(new ArrayList<>(goals.values()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable UUID id) {
        Goal goal = goals.get(id);
        return goal != null ? ResponseEntity.ok(goal) : ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal) {
        UUID id = UUID.randomUUID();
        goal.setId(id);
        goals.put(id, goal);
        return ResponseEntity.ok(goal);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable UUID id, @RequestBody Goal goal) {
        if (!goals.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        goal.setId(id);
        goals.put(id, goal);
        return ResponseEntity.ok(goal);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable UUID id) {
        if (!goals.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        goals.remove(id);
        return ResponseEntity.noContent().build();
    }
}

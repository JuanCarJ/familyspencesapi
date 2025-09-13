package com.familyspencesapi.controllers.goal;

import com.familyspencesapi.domain.goals.Goal;
import com.familyspencesapi.service.goals.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    // Constructor con inyección de dependencias
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // GET: traer todas las metas
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        List<Goal> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }

    // GET: traer meta por id
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable UUID id) {
        return goalService.getGoalById(id)
                .map(ResponseEntity::ok) // si existe la meta
                .orElse(ResponseEntity.notFound().build()); // si no existe
    }

    // POST: crear una nueva meta
    @PostMapping
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody Goal goal) {
        Goal savedGoal = goalService.createGoal(goal);
        return ResponseEntity.ok(savedGoal);
    }

    // PUT: actualizar una meta existente
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable UUID id, @Valid @RequestBody Goal goalDetails) {
        return goalService.updateGoal(id, goalDetails)
                .map(ResponseEntity::ok) // devuelve la meta actualizada
                .orElse(ResponseEntity.notFound().build()); // si no existe
    }

    // DELETE: eliminar meta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable UUID id) {
        boolean deleted = goalService.deleteGoal(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

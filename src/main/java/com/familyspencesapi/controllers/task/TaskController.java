package com.familyspencesapi.controllers.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.messages.task.TaskMessageSender;
import com.familyspencesapi.service.task.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMessageSender taskMessagePublisher;

    public TaskController(TaskService taskService, TaskMessageSender taskMessagePublisher) {
        this.taskService = taskService;
        this.taskMessagePublisher = taskMessagePublisher;
    }

    // ============= QUERIES (Síncronas) =============

    @GetMapping("/all")
    public ResponseEntity<List<Tasks>> getAllTasks(@RequestParam UUID familyId) {
        List<Tasks> listOfTasks = taskService.getAllTasks(familyId);
        return ResponseEntity.ok(listOfTasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Tasks> getTask(@RequestParam UUID familyId, @PathVariable UUID taskId) {
        Tasks task = taskService.getTask(familyId, taskId);
        return ResponseEntity.ok(task);
    }

    // ============= COMMANDS (Síncronos + Evento asíncrono) =============

    @PostMapping
    public ResponseEntity<Tasks> createTask(@RequestParam UUID familyId, @RequestBody Tasks task) {
        try {
            task.setFamilyId(familyId);

            // 1. Validar y persistir en BD (síncrono)
            Tasks createdTask = taskService.createTask(task);

            // 2. Publicar evento para que el procesador lo maneje (asíncrono)
            taskMessagePublisher.publishTaskCreated(createdTask);

            return ResponseEntity.ok(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Tasks> updateTask(
            @RequestParam UUID familyId,
            @PathVariable UUID taskId,
            @RequestBody Tasks task) {
        try {
            task.setFamilyId(familyId);

            // 1. Validar y actualizar en BD (síncrono)
            Tasks updatedTask = taskService.updateTask(taskId, task);

            // 2. Publicar evento para que el procesador lo maneje (asíncrono)
            taskMessagePublisher.publishTaskUpdated(updatedTask);

            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@RequestParam UUID familyId, @PathVariable UUID taskId) {
        try {
            // 1. Validar y eliminar de BD (síncrono)
            taskService.deleteTask(familyId, taskId);

            // 2. Publicar evento para que el procesador lo maneje (asíncrono)
            taskMessagePublisher.publishTaskDeleted(
                    java.util.Map.of("familyId", familyId.toString(), "taskId", taskId.toString())
            );

            return ResponseEntity.ok("Task deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
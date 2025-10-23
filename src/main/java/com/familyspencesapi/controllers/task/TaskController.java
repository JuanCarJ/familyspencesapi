package com.familyspencesapi.controllers.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.messages.task.TaskMessageSender;
import com.familyspencesapi.service.task.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMessageSender taskMessageSender;

    public TaskController(TaskService taskService, TaskMessageSender taskMessageSender) {
        this.taskService = taskService;
        this.taskMessageSender = taskMessageSender;
    }


    @GetMapping
    public ResponseEntity<List<Tasks>> getAllTasks(@RequestParam UUID familyId) {
        List<Tasks> tasks = taskService.getAllTasks(familyId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Tasks> getTask(
            @RequestParam UUID familyId,
            @PathVariable UUID taskId
    ) {
        Tasks task = taskService.getTask(familyId, taskId);
        return ResponseEntity.ok(task);
    }


    @PostMapping
    public ResponseEntity<?> createTask(
            @RequestParam UUID familyId,
            @RequestBody Tasks task
    ) {
        try {
            task.setFamilyId(familyId);

            // 1️⃣ Crear la tarea en la BD (proceso síncrono)
            Tasks createdTask = taskService.createTask(task);

            // 2️⃣ Enviar evento a RabbitMQ (proceso asíncrono)
            taskMessageSender.sendTaskCreated(createdTask);

            return ResponseEntity.ok(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error"));
        }
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @RequestParam UUID familyId,
            @PathVariable UUID taskId,
            @RequestBody Tasks task
    ) {
        try {
            task.setFamilyId(familyId);

            // 1️⃣ Actualizar en la BD
            Tasks updatedTask = taskService.updateTask(taskId, task);

            // 2️⃣ Enviar evento
            taskMessageSender.sendTaskUpdated(updatedTask);

            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error"));
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @RequestParam UUID familyId,
            @PathVariable UUID taskId
    ) {
        try {
            // 1️⃣ Eliminar en la BD
            taskService.deleteTask(familyId, taskId);

            // 2️⃣ Publicar evento al Consumer
            taskMessageSender.sendTaskDeleted(
                    Map.of("familyId", familyId.toString(), "taskId", taskId.toString())
            );

            return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error"));
        }
    }
}

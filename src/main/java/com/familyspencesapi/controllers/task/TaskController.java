package com.familyspencesapi.controllers.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.messages.task.TaskMessageSender;
import com.familyspencesapi.service.task.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMessageSender taskMessageSender;
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_KEY = "error";

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
    public ResponseEntity<Object> createTask(
            @RequestParam UUID familyId,
            @RequestBody Tasks task
    ) {
        try {
            task.setFamilyId(familyId);

            Tasks createdTask = taskService.createTask(task);

            taskMessageSender.sendTaskCreated(createdTask);

            return ResponseEntity.ok(createdTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<Object> updateTask(
            @RequestParam UUID familyId,
            @PathVariable UUID taskId,
            @RequestBody Tasks task
    ) {
        try {
            task.setFamilyId(familyId);

            Tasks updatedTask = taskService.updateTask(taskId, task);

            taskMessageSender.sendTaskUpdated(updatedTask);

            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Object> deleteTask(
            @RequestParam UUID familyId,
            @PathVariable UUID taskId
    ) {
        try {
            taskService.deleteTask(familyId, taskId);

            taskMessageSender.sendTaskDeleted(
                    Map.of("familyId", familyId.toString(), "taskId", taskId.toString())
            );

            return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }
}

package com.familyspencesapi.controllers.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.messages.task.TaskMessagePublisher;
import com.familyspencesapi.service.task.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMessagePublisher taskMessagePublisher;

    public TaskController(TaskService taskService, TaskMessagePublisher taskMessagePublisher) {
        this.taskService = taskService;
        this.taskMessagePublisher = taskMessagePublisher;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Tasks>> getAllTasks(@RequestParam UUID familyId) {
        List<Tasks> listOfTasks = taskService.getAllTasks(familyId);
        return ResponseEntity.ok(listOfTasks);
    }

    @PostMapping
    public ResponseEntity<String> postTask(@RequestParam UUID familyId, @RequestBody Tasks task) {
        task.setFamilyId(familyId);
        taskMessagePublisher.publishTaskCreated(task);
        return ResponseEntity.ok("Mensaje de creación de Task enviado a RabbitMQ.");
    }

    @PutMapping
    public ResponseEntity<String> putTask(@RequestParam UUID familyId, @RequestParam UUID taskId, @RequestBody Tasks task) {
        task.setFamilyId(familyId);
        task.setId(taskId);
        taskMessagePublisher.publishTaskUpdated(task);
        return ResponseEntity.ok("Mensaje de actualización enviado a RabbitMQ.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTask(@RequestParam UUID familyId, @RequestParam UUID taskId) {
        taskMessagePublisher.publishTaskDeleted(
                java.util.Map.of("familyId", familyId, "taskId", taskId)
        );
        return ResponseEntity.ok("Mensaje de eliminación enviado a RabbitMQ.");
    }
}

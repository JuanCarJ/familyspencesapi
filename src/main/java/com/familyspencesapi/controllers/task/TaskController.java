package com.familyspencesapi.controllers.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/all")
    public ResponseEntity<List<Tasks>> getAllTasks(@RequestParam(required = true) UUID familyId){
        List<Tasks> listOfTasks = taskService.getAllTasks(familyId);

        return ResponseEntity.ok(listOfTasks);
    }
    @GetMapping
    public ResponseEntity<Tasks> getTask(@RequestParam(required = true) UUID familyId, @RequestParam(required = true) UUID taskId){
        Tasks tasks = taskService.getTask(familyId,taskId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<String> postTask(@RequestParam(required = true) UUID familyId ,@RequestBody Tasks task){

        String responseTask = taskService.saveTask(familyId,task);

        return ResponseEntity.ok(responseTask);
    }

    @PutMapping
    public ResponseEntity<String> putTask(@RequestParam(required = true) UUID familyId ,@RequestParam(required = true) UUID taskId,@RequestBody Tasks task){

        String responseTask = taskService.updateTask(familyId,taskId,task);

        return ResponseEntity.ok(responseTask);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTask(@RequestParam(required = true) UUID familyId ,@RequestParam(required = true) UUID taskId){
        String responseTask = taskService.deleteTask(familyId,taskId);

        return ResponseEntity.ok(responseTask);
    }

}

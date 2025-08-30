package com.familyspencesapi.controllers.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.mock.tasks.TaskMockData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TaskController {

    @GetMapping("/tasks/all")
    public ResponseEntity<List<Tasks>> getAllTasks(@RequestParam(required = true) UUID familyId){
        List<Tasks> listOfTasks = TaskMockData.getAllMockTasks();;

        return ResponseEntity.ok(listOfTasks);
    }
    @GetMapping("/tasks")
    public ResponseEntity<Tasks> getTask(@RequestParam(required = true) UUID familyId, @RequestParam(required = true) UUID taskId){
        Tasks tasks = TaskMockData.getMockTasks();

        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> postTask(@RequestParam(required = true) UUID familyId ,@RequestBody Tasks task){
        return ResponseEntity.ok("Task registered successfully");
    }

    @PutMapping("/tasks")
    public ResponseEntity<String> putTask(@RequestParam(required = true) UUID familyId ,@RequestParam(required = true) UUID taskID,@RequestBody Tasks task){
        return ResponseEntity.ok("Task updated successfully");
    }

    @DeleteMapping ("/tasks")
    public ResponseEntity<String> deleteTask(@RequestParam(required = true) UUID familyId ,@RequestParam(required = true) UUID taskID){
        return ResponseEntity.ok("Task deleted successfully");
    }

}

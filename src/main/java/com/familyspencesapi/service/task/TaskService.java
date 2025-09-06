package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.mock.tasks.TaskMockData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final List<Tasks> tasks;

    public TaskService() {
        this.tasks = TaskMockData.getAllMockTasks();
    }

    public List<Tasks> getAllTasks(final UUID familyId){
        boolean familyExists = tasks.stream()
                .anyMatch(t -> familyId.equals(t.getFamiliId()));

        if (!familyExists) {
            throw new IllegalArgumentException("Family identification does not exist");
        }
        return tasks.stream().filter(t -> t.getFamiliId().equals(familyId)).toList();
    }

    public Tasks getTask(final UUID familyId, final UUID taskId){

        return tasks.stream().filter(t -> taskId.equals(t.getId()) && familyId.equals(t.getFamiliId()))
                .findFirst().orElseThrow(()-> new IllegalArgumentException("Family or task identification does not exist")) ;
    }

    public String saveTask(final UUID familyId, final Tasks task){
        if (tasks.stream().noneMatch(t -> t.getFamiliId().equals(familyId))) {
            throw new IllegalArgumentException("Family identification does not exist");
        }

        if (tasks.stream().noneMatch(t -> t.getId().equals(task.getId()))) {
            throw new IllegalArgumentException("Task with same id");
        }
        tasks.add(task);
        return "Task registered successfully";
    }

    public String updateTask(final UUID familyId, final UUID taskId, final Tasks task){
        Tasks existingTask = tasks.stream()
                .filter(t -> t.getFamiliId().equals(familyId) && t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Family identification or task id does not exist"));

        BeanUtils.copyProperties(task,existingTask,"id","familyId");
        return "Task updated successfully";
    }

    public String deleteTask(final UUID familyId, final UUID taskId){

        boolean tasksExist = tasks.removeIf(t -> t.getId().equals(taskId) && t.getFamiliId().equals(familyId));

        if(!tasksExist){
            throw new IllegalArgumentException("Family identification or task id does not exist");
        }

        return "Task deleted successfully";
    }
    
}

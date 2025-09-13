package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.mock.tasks.TaskMockData;
import com.familyspencesapi.repositories.task.ITaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private ITaskRepository iTaskRepository;

    private final List<Tasks> tasks;

    public TaskService(ITaskRepository iTaskRepository) {
        this.iTaskRepository = iTaskRepository;
        this.tasks = TaskMockData.getAllMockTasks();
    }

    public List<Tasks> getAllTasks(final UUID familyId){

        List<Tasks> tasksList = iTaskRepository.findByFamilyId(familyId);

        if (tasksList.isEmpty()) {
            throw new IllegalArgumentException("Family identification does not exist");
        }
        return tasksList;
    }

    public Tasks getTask(final UUID familyId, final UUID taskId){

        return tasks.stream().filter(t -> taskId.equals(t.getId()) && familyId.equals(t.getFamilyId()))
                .findFirst().orElseThrow(()-> new IllegalArgumentException("Family or task identification does not exist")) ;
    }

    public String saveTask(final UUID familyId, final Tasks task){
        if (tasks.stream().noneMatch(t -> t.getId().equals(task.getId()))) {
            throw new IllegalArgumentException("Family identification does not exist");
        }

        if (tasks.stream().noneMatch(t -> t.getId().equals(task.getId()))) {
            throw new IllegalArgumentException("Task with same id");
        }
        iTaskRepository.save(task);
        return "Task registered successfully";
    }

    public String updateTask(final UUID familyId, final UUID taskId, final Tasks task){
        Tasks existingTask = tasks.stream()
                .filter(t -> t.getFamilyId().equals(familyId) && t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Family identification or task id does not exist"));

        BeanUtils.copyProperties(task,existingTask,"id","familyId");
        return "Task updated successfully";
    }

    public String deleteTask(final UUID familyId, final UUID taskId){

        boolean tasksExist = tasks.removeIf(t -> t.getId().equals(taskId) && t.getFamilyId().equals(familyId));

        iTaskRepository.deleteById(taskId);

        if(!tasksExist){
            throw new IllegalArgumentException("Family identification or task id does not exist");
        }

        return "Task deleted successfully";
    }
    
}

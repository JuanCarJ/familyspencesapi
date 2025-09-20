package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.task.ITaskRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.vacation.VacationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final ITaskRepository iTaskRepository;
    private final FamilyRepository familyRepository;
    private final ExpenseRepository expenseRepository;
    private final VacationRepository vacationRepository;

    public TaskService(ITaskRepository iTaskRepository, FamilyRepository familyRepository, ExpenseRepository expenseRepository, VacationRepository vacationRepository) {
        this.iTaskRepository = iTaskRepository;
        this.familyRepository = familyRepository;
        this.expenseRepository = expenseRepository;
        this.vacationRepository = vacationRepository;
    }

    public List<Tasks> getAllTasks(final UUID familyId){

        List<Tasks> tasksList = iTaskRepository.findByFamilyId(familyId);

        if(tasksList.isEmpty()){
            throw new IllegalArgumentException("Family identification does not exist");
        }

        return tasksList;
    }


    public Tasks getTask(final UUID familyId, final UUID taskId){

        if(familyRepository.findById(familyId).isEmpty()){
            throw new IllegalArgumentException("Family not found");
        }

        return iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found "));
    }


    public String saveTask(final UUID familyId, final Tasks task){

        if (task.getIdVacations() != null && task.getIdExpenseve() != null) {
            throw new IllegalArgumentException("A task cannot have both Vacation and Expenseve");
        }

        if(familyRepository.findById(familyId).isEmpty()){
            throw new IllegalArgumentException("Family not found");
        }
        iTaskRepository.save(task);

        return "Task registered successfully";
    }


    public String updateTask(final UUID familyId, final UUID taskId, Tasks task) {
        if (familyRepository.findById(familyId).isEmpty()) {
            throw new IllegalArgumentException("Family not found");
        }
        Tasks taskUpdate = iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        taskUpdate.setName(task.getName());
        taskUpdate.setDescription(task.getDescription());
        taskUpdate.setStatus(taskUpdate.isStatus());

        if (task.getIdVacations() != null && task.getIdExpenseve() != null) {
            throw new IllegalArgumentException("A task cannot have both Vacation and Expenseve");
        }
        if (task.getIdVacations() != null) {
            taskUpdate.setIdVacations(
                    vacationRepository.findById(task.getIdVacations().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Vacation not found"))
            );
            taskUpdate.setIdExpenseve(null);
        }

        if (task.getIdExpenseve() != null) {
            taskUpdate.setIdExpenseve(
                    expenseRepository.findById(task.getIdExpenseve().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Expenseve not found"))
            );
            taskUpdate.setIdVacations(null);
        }
        iTaskRepository.save(taskUpdate);
        return "Task updated successfully";
    }


    public String deleteTask(final UUID familyId, final UUID taskId) {

        if(familyRepository.findById(familyId).isEmpty()){
            throw new IllegalArgumentException("Family not found");
        }

        Tasks tasksExist = iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if(tasksExist == null){
            throw new IllegalArgumentException("Family identification or task id does not exist");
        }

        iTaskRepository.deleteById(taskId);

        return "Task deleted successfully";
    }
    
}

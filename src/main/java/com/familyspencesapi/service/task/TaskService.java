package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.task.ITaskRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.vacation.VacationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final ITaskRepository iTaskRepository;
    private final FamilyRepository familyRepository;
    private final ExpenseRepository expenseRepository;
    private final VacationRepository vacationRepository;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private static final String TASK_NOT_FOUND = "Task not found";
    private static final String FAMILY_NOT_FOUND = "Family not found";



    public TaskService(ITaskRepository iTaskRepository, FamilyRepository familyRepository,
                       ExpenseRepository expenseRepository, VacationRepository vacationRepository) {
        this.iTaskRepository = iTaskRepository;
        this.familyRepository = familyRepository;
        this.expenseRepository = expenseRepository;
        this.vacationRepository = vacationRepository;
    }


    public List<Tasks> getAllTasks(final UUID familyId) {
        if (familyRepository.findById(familyId).isEmpty()) {
            throw new IllegalArgumentException(FAMILY_NOT_FOUND);
        }
        List<Tasks> tasksList = iTaskRepository.findByFamilyId(familyId);
        return tasksList;
    }

    public Tasks getTask(final UUID familyId, final UUID taskId) {
        if (familyRepository.findById(familyId).isEmpty()) {
            throw new IllegalArgumentException(FAMILY_NOT_FOUND);
        }

        return iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));
    }

    @Transactional
    public Tasks createTask(final Tasks task) {
        log.info(" Validando y creando task...");

        if (task.getIdVacations() != null && task.getIdExpenseve() != null) {
            throw new IllegalArgumentException("A task cannot have both Vacation and Expense");
        }

        if (familyRepository.findById(task.getFamilyId()).isEmpty()) {
            throw new IllegalArgumentException(FAMILY_NOT_FOUND);
        }

        if (task.getIdExpenseve() != null) {
            expenseRepository.findById(task.getIdExpenseve().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        }

        if (task.getIdVacations() != null) {
            vacationRepository.findById(task.getIdVacations().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Vacation not found"));
        }

        Tasks savedTask = iTaskRepository.save(task);
        log.info(" Task creada: {}", savedTask.getId());
        return savedTask;
    }

    @Transactional
    public Tasks updateTask(final UUID taskId, final Tasks task) {
        log.info(" Validando y actualizando task: {}", taskId);

        if (familyRepository.findById(task.getFamilyId()).isEmpty()) {
            throw new IllegalArgumentException(FAMILY_NOT_FOUND);
        }

        Tasks taskUpdate = iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));

        taskUpdate.setName(task.getName());
        taskUpdate.setDescription(task.getDescription());
        taskUpdate.setStatus(task.isStatus());

        if (task.getIdVacations() != null && task.getIdExpenseve() != null) {
            throw new IllegalArgumentException("A task cannot have both Vacation and Expense");
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
                            .orElseThrow(() -> new IllegalArgumentException("Expense not found"))
            );
            taskUpdate.setIdVacations(null);
        }

        Tasks updatedTask = iTaskRepository.save(taskUpdate);
        log.info(" Task actualizada: {}", updatedTask.getId());
        return updatedTask;
    }

    @Transactional
    public void deleteTask(final UUID familyId, final UUID taskId) {
        log.info(" Validando y eliminando task: {}", taskId);

        if (familyRepository.findById(familyId).isEmpty()) {
            throw new IllegalArgumentException(FAMILY_NOT_FOUND);
        }
        iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(TASK_NOT_FOUND));

        iTaskRepository.deleteById(taskId);
        log.info(" Task eliminada: {}", taskId);

    }
}
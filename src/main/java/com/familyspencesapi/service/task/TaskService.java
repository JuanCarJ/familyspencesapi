package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.task.ITaskRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.vacation.VacationRepository;
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

    public TaskService(ITaskRepository iTaskRepository, FamilyRepository familyRepository,
                       ExpenseRepository expenseRepository, VacationRepository vacationRepository) {
        this.iTaskRepository = iTaskRepository;
        this.familyRepository = familyRepository;
        this.expenseRepository = expenseRepository;
        this.vacationRepository = vacationRepository;
    }

    // ============= QUERIES =============

    public List<Tasks> getAllTasks(final UUID familyId) {
        List<Tasks> tasksList = iTaskRepository.findByFamilyId(familyId);

        if (tasksList.isEmpty()) {
            throw new IllegalArgumentException("Family identification does not exist");
        }

        return tasksList;
    }

    public Tasks getTask(final UUID familyId, final UUID taskId) {
        if (familyRepository.findById(familyId).isEmpty()) {
            throw new IllegalArgumentException("Family not found");
        }

        return iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    // ============= COMMANDS =============

    @Transactional
    public Tasks createTask(final Tasks task) {
        System.out.println("🔧 Validando y creando task...");

        // Validación: No puede tener vacation y expense al mismo tiempo
        if (task.getIdVacations() != null && task.getIdExpenseve() != null) {
            throw new IllegalArgumentException("A task cannot have both Vacation and Expense");
        }

        // Validar que la familia existe
        if (familyRepository.findById(task.getFamilyId()).isEmpty()) {
            throw new IllegalArgumentException("Family not found");
        }

        // Validar expense si existe
        if (task.getIdExpenseve() != null) {
            expenseRepository.findById(task.getIdExpenseve().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        }

        // Validar vacation si existe
        if (task.getIdVacations() != null) {
            vacationRepository.findById(task.getIdVacations().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Vacation not found"));
        }

        Tasks savedTask = iTaskRepository.save(task);
        System.out.println("✅ Task creada: " + savedTask.getId());
        return savedTask;
    }

    @Transactional
    public Tasks updateTask(final UUID taskId, final Tasks task) {
        System.out.println("🔧 Validando y actualizando task: " + taskId);

        // Validar que la familia existe
        if (familyRepository.findById(task.getFamilyId()).isEmpty()) {
            throw new IllegalArgumentException("Family not found");
        }

        // Buscar la task existente
        Tasks taskUpdate = iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Actualizar campos básicos
        taskUpdate.setName(task.getName());
        taskUpdate.setDescription(task.getDescription());
        taskUpdate.setStatus(task.isStatus());

        // Validación: No puede tener vacation y expense al mismo tiempo
        if (task.getIdVacations() != null && task.getIdExpenseve() != null) {
            throw new IllegalArgumentException("A task cannot have both Vacation and Expense");
        }

        // Actualizar vacation
        if (task.getIdVacations() != null) {
            taskUpdate.setIdVacations(
                    vacationRepository.findById(task.getIdVacations().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Vacation not found"))
            );
            taskUpdate.setIdExpenseve(null);
        }

        // Actualizar expense
        if (task.getIdExpenseve() != null) {
            taskUpdate.setIdExpenseve(
                    expenseRepository.findById(task.getIdExpenseve().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Expense not found"))
            );
            taskUpdate.setIdVacations(null);
        }

        Tasks updatedTask = iTaskRepository.save(taskUpdate);
        System.out.println("✅ Task actualizada: " + updatedTask.getId());
        return updatedTask;
    }

    @Transactional
    public void deleteTask(final UUID familyId, final UUID taskId) {
        System.out.println("🔧 Validando y eliminando task: " + taskId);

        // Validar que la familia existe
        if (familyRepository.findById(familyId).isEmpty()) {
            throw new IllegalArgumentException("Family not found");
        }

        // Validar que la task existe
        Tasks tasksExist = iTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        iTaskRepository.deleteById(taskId);
        System.out.println("✅ Task eliminada: " + taskId);
    }
}
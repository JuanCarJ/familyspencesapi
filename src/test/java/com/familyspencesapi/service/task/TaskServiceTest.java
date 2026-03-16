package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.task.ITaskRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.vacation.VacationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private ITaskRepository taskRepository;
    @Mock
    private FamilyRepository familyRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private VacationRepository vacationRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testGetTaskFound() {
        UUID familyId = UUID.randomUUID();
        Tasks task = new Tasks();
        task.setId(UUID.randomUUID());
        task.setFamilyId(familyId);
        task.setName("Comprar alimentos");

        when(familyRepository.findById(familyId)).thenReturn(Optional.of(new Family()));
        when(taskRepository.findByFamilyId(familyId)).thenReturn(List.of(task));

        List<Tasks> result = taskService.getAllTasks(familyId);

        assertEquals(1, result.size());
        assertEquals("Comprar alimentos", result.getFirst().getName());
    }

    @Test
    void testGetAllTasksWhenNoTasks() {
        UUID familyId = UUID.randomUUID();

        when(familyRepository.findById(familyId)).thenReturn(Optional.of(new Family()));
        when(taskRepository.findByFamilyId(familyId)).thenReturn(List.of());

        List<Tasks> result = taskService.getAllTasks(familyId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllTasksFamilyNotFound() {
        UUID familyId = UUID.randomUUID();

        when(familyRepository.findById(familyId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> taskService.getAllTasks(familyId));
    }

}

package com.familyspencesapi.service.task;

import com.familyspencesapi.domain.tasks.Tasks;
import com.familyspencesapi.repositories.task.ITaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private ITaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void testGetTaskFound() {
        UUID familyId = UUID.randomUUID();
        Tasks task = new Tasks();
        task.setId(UUID.randomUUID());
        task.setFamilyId(familyId);
        task.setName("Comprar alimentos");

        when(taskRepository.findByFamilyId(familyId)).thenReturn(List.of(task));

        List<Tasks> result = taskService.getAllTasks(familyId);

        assertEquals(1, result.size());
        assertEquals("Comprar alimentos", result.getFirst().getName());
    }

    @Test
    void testGetAllTasksWhenNoTasks() {
        UUID familyId = UUID.randomUUID();

        when(taskRepository.findByFamilyId(familyId)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> taskService.getAllTasks(familyId));
    }

}

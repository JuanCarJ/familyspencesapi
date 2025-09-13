package com.familyspencesapi.mock.tasks;
import com.familyspencesapi.domain.tasks.Tasks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskMockData {

    public static List<Tasks> getAllMockTasks() {
        List<Tasks> tasks = new ArrayList<>();

        Tasks t1 = new Tasks();

        Tasks t2 = new Tasks();

        tasks.add(t1);
        tasks.add(t2);

        return tasks;
    }

    public static Tasks getMockTasks() {
        Tasks t1 = new Tasks();
        return t1;
    }
}

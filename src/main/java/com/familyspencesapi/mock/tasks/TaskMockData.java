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
        t1.setId(UUID.fromString("11111111-2222-3333-4444-555555555553"));
        t1.setFamiliId(UUID.fromString("11111111-2222-3333-4444-55555555555"));
        t1.setName("Comprar alimentos");
        t1.setDescription("Ir al supermercado y comprar frutas y verduras");
        t1.setStatus(false);
        t1.setCreationDate(LocalDate.now());
        t1.setIdResponsible(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"));
        t1.setIdVacations(UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff"));
        t1.setIdExpenseve(UUID.fromString("cccccccc-dddd-eeee-ffff-111111111111"));

        Tasks t2 = new Tasks();
        t2.setId(UUID.fromString("22222222-3333-4444-5555-666666666666"));
        t2.setFamiliId(UUID.fromString("11111111-2222-3333-4444-555555555553"));
        t2.setName("Lavar los platos");
        t2.setDescription("Lavar los platos después de la cena");
        t2.setStatus(true);
        t2.setCreationDate(LocalDate.now().minusDays(1));
        t2.setIdResponsible(UUID.fromString("dddddddd-eeee-ffff-1111-222222222222"));
        t2.setIdVacations(UUID.fromString("eeeeeeee-ffff-1111-2222-333333333333"));
        t2.setIdExpenseve(UUID.fromString("ffffffff-1111-2222-3333-444444444444"));

        tasks.add(t1);
        tasks.add(t2);

        return tasks;
    }

    public static Tasks getMockTasks() {
        Tasks t1 = new Tasks();
        t1.setId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        t1.setName("Comprar alimentos");
        t1.setDescription("Ir al supermercado y comprar frutas y verduras");
        t1.setStatus(false);
        t1.setCreationDate(LocalDate.now());
        t1.setIdResponsible(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"));
        t1.setIdVacations(UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff"));
        t1.setIdExpenseve(UUID.fromString("cccccccc-dddd-eeee-ffff-111111111111"));

        return t1;
    }
}

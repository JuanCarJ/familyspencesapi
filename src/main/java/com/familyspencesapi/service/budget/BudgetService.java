package com.familyspencesapi.service.budget;// Archivo: BudgetService.java

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
public class BudgetService {

    public Map<String, Object> createBudget(UUID familyId, CreateBudgetRequest request) {
        System.out.println("Petecion llego" + familyId );
        System.out.println("Datos recibidos del postman " + request);

        return Map.of(
                "budgetId", UUID.randomUUID(),
                "familyId", familyId,
                "period", request.period(),
                "budgetAmount", request.budgetAmount(),
                "responsibleId", request.responsibleId(),
                "message", "Budget successfully added from Service"
        );
    }

    public Map<String, Object> getBudgetDetails(UUID budgetId) {
        System.out.println("Presupuesto con ID " + budgetId );
        return Map.of(
                "budgetId", budgetId,
                "period", "2025-09-05",
                "budgetAmount", 2000000,
                "responsible", Map.of(
                        "responsibleId", UUID.randomUUID(),
                        "name", "Mom"
                ),
                "summary", Map.of(
                        "familyTotalIncome", 3500000,
                        "totalExpenses", 1600000,
                        "balance", 1900000
                )
        );
    }
}
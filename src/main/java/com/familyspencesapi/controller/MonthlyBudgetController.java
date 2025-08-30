package com.familyspencesapi.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class MonthlyBudgetController {

    public record CreateBudgetRequest(LocalDate period, double budgetAmount, UUID responsibleId) {}


    @PostMapping("/families/{familyId}/budgets")
    public Map<String, Object> createBudget(@PathVariable  UUID familyId,
                                            @RequestBody CreateBudgetRequest datosDelBody) {

        System.out.println("Petición llego: " + familyId);
        System.out.println("Datos recibidos del postman " + datosDelBody);

        Map<String, Object> respuestaPost = Map.of(
                "budgetId", UUID.randomUUID(),
                "familyId", familyId,
                "period", datosDelBody.period(),
                "budgetAmount", datosDelBody.budgetAmount(),
                "responsibleId", datosDelBody.responsibleId(),
                "message", "Budget successfully added"
        );

        return respuestaPost;
    }

    @GetMapping("/budgets/{budgetId}")
    public Map<String, Object> getBudgetDetails(@PathVariable UUID budgetId) {

        System.out.println("Presupuesto con ID: " + budgetId);

        Map<String, Object> respuestaGet = Map.of(
                "budgetId", "1e7b2c95-3652-468e-9c02-7489e81b617c",
                "period", "2023-12-25",
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

        return respuestaGet;
    }
}
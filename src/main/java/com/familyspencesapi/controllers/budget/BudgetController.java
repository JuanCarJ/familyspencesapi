
package com.familyspencesapi.controllers.budget;

import com.familyspencesapi.service.budget.BudgetService;
import com.familyspencesapi.service.budget.CreateBudgetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/families/{familyId}/budgets")
    public Map<String, Object> createBudget(@PathVariable UUID familyId,
                                            @RequestBody CreateBudgetRequest datosDelBody) {
        return budgetService.createBudget(familyId, datosDelBody);
    }

    @GetMapping("/budgets/{budgetId}")
    public Map<String, Object> getBudgetDetails(@PathVariable UUID budgetId) {
        return budgetService.getBudgetDetails(budgetId);
    }
    @GetMapping("/families/{familyId}/budgets")
    public List<Map<String, Object>> getAllBudgets(@PathVariable UUID familyId) {
        return budgetService.getAllBudgetsForFamily(familyId);
    }

}
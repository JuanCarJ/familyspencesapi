package com.familyspencesapi.controllers.income;

import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.service.income.IncomeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping("/all")
    public List<Income> getAllIncomes() {
        return incomeService.getAllIncomes();
    }

    @PostMapping
    public Income createIncome(@RequestBody Income income) {
        return incomeService.createIncome(income);
    }

    @GetMapping("/{id}")
    public Optional<Income> getIncomeById(@PathVariable Long id) {
        return incomeService.getIncomeById(id);
    }

    @PutMapping("/{id}")
    public Income updateIncome(@PathVariable Long id, @RequestBody Income updatedIncome) {
        return incomeService.updateIncome(id, updatedIncome)
                .orElseThrow(() -> new RuntimeException("Income with ID " + id + " not found"));
    }

    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Long id) {
        return incomeService.deleteIncome(id) ? "Income deleted successfully" : "Income not found";
    }

    @GetMapping("/family/{familyId}")
    public List<Income> getIncomesByFamilyId(@PathVariable UUID familyId) {
        return incomeService.getIncomesByFamilyId(familyId);
    }

    @GetMapping("/family/{familyId}/total")
    public Double getTotalByFamilyId(@PathVariable UUID familyId) {
        return incomeService.getTotalByFamilyId(familyId);
    }
}

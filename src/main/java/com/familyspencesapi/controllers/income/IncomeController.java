package com.familyspencesapi.controllers.income;


import com.familyspencesapi.domain.income.Income;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final List<Income> incomes = new ArrayList<>();
    private Long idCounter = 1L;

    @GetMapping("/all")
    public List<Income> getAllIncomes() {
        return incomes;
    }

    @PostMapping
    public String createIncome(@RequestBody Income income) {
        income.setId(idCounter++);
        incomes.add(income);
        return "Income registered successfully with ID: " + income.getId();
    }

    @PutMapping("/{id}")
    public String updateIncome(@PathVariable Long id, @RequestBody Income updatedIncome) {
        for (Income income : incomes) {
            if (income.getId().equals(id)) {
                income.setResponsible(updatedIncome.getResponsible());
                income.setTitle(updatedIncome.getTitle());
                income.setDescription(updatedIncome.getDescription());
                income.setPeriod(updatedIncome.getPeriod());
                income.setTotal(updatedIncome.getTotal());
                income.setFamilyId(updatedIncome.getFamilyId());
                return "Income updated successfully";
            }
        }
        return "Income with ID " + id + " not found";
    }

    @DeleteMapping("/{id}")
    public String deleteIncome(@PathVariable Long id) {
        boolean removed = incomes.removeIf(i -> i.getId().equals(id));
        return removed ? "Income deleted successfully" : "Income not found";
    }
}

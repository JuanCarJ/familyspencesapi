package com.familyspencesapi.controllers.income;
import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.service.income.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public ResponseEntity<List<Income>> getAllIncomes() {
        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Income>> getByFamily(@PathVariable UUID familyId) {
        return ResponseEntity.ok(incomeService.getIncomesByFamily(familyId));
    }

    @GetMapping("/responsible/{responsibleId}")
    public ResponseEntity<List<Income>> getByResponsible(@PathVariable UUID responsibleId) {
        return ResponseEntity.ok(incomeService.getIncomesByResponsible(responsibleId));
    }

    @GetMapping("/period/{period}")
    public ResponseEntity<List<Income>> getByPeriod(@PathVariable String period) {
        return ResponseEntity.ok(incomeService.getIncomesByPeriod(period));
    }

    @PostMapping
    public ResponseEntity<Income> createIncome(@RequestBody Income income) {
        Income created = incomeService.createIncome(income);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Income> updateIncome(@PathVariable UUID id, @RequestBody Income income) {
        Income updatedIncome = incomeService.updateIncome(id, income);
        if (updatedIncome == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedIncome);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable UUID id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
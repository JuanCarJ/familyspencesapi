package com.familyspencesapi.controllers.income;

import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.service.income.IncomeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    //Obtener todos los ingresos
    @GetMapping("/all")
    public ResponseEntity<List<Income>> getAllIncomes() {
        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    //Crear un nuevo ingreso
    @PostMapping
    public ResponseEntity<Income> createIncome(@RequestBody Income income) {
        Income savedIncome = incomeService.createIncome(income);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIncome);
    }

    //Buscar ingreso por ID
    @GetMapping("/{id}")
    public ResponseEntity<Income> getIncomeById(@PathVariable Long id) {
        return incomeService.getIncomeById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID " + id + " not found"));
    }

    //Actualizar ingreso
    @PutMapping("/{id}")
    public ResponseEntity<Income> updateIncome(@PathVariable Long id, @RequestBody Income updatedIncome) {
        return incomeService.updateIncome(id, updatedIncome)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID " + id + " not found"));
    }

    //Eliminar ingreso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        if (incomeService.deleteIncome(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID " + id + " not found");
        }
    }

    @GetMapping("/responsible/id/{responsibleId}")
    public List<Income> getIncomesByResponsibleId(@PathVariable UUID responsibleId) {
        return incomeService.getIncomesByResponsibleId(responsibleId);
    }

    //Obtener ingresos por familia
    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<Income>> getIncomesByFamilyId(@PathVariable UUID familyId) {
        return ResponseEntity.ok(incomeService.getIncomesByFamilyId(familyId));
    }

    //Obtener total de ingresos por familia
    @GetMapping("/family/{familyId}/total")
    public ResponseEntity<Double> getTotalByFamilyId(@PathVariable UUID familyId) {
        Double total = incomeService.getTotalByFamilyId(familyId);
        return ResponseEntity.ok(total != null ? total : 0.0);
    }
}

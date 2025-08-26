package com.familyspencesapi.controllers;

import com.familyspencesapi.domain.expenseControl.Expense;
import com.familyspencesapi.domain.family.FamilyMember;
import com.familyspencesapi.repositories.ExpenseRepository;
import com.familyspencesapi.repositories.FamilyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rest")
public class ExpenseControlController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FamilyRepository familyRepository;

    // GET: api/v1/rest/expenses
    @GetMapping("/expenses")
    public List<Expense> getAll() {
        return expenseRepository.findAll();
    }

    // POST: api/v1/rest/expenses
    @PostMapping("/expenses")
    public ResponseEntity<?> create(@Valid @RequestBody ExpenseRequest request) {
        FamilyMember responsable = familyRepository.findById(request.getIdFamily())
                .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getDescription(),
                request.getPeriod(),
                responsable,
                BigDecimal.valueOf(request.getValue())
        );

        Expense saved = expenseRepository.save(expense);

        return ResponseEntity.ok(new ApiResponse("Gasto registrado correctamente", saved.getId().toString()));
    }

    // PUT: api/v1/rest/expenses/{id}
    @PutMapping("/expenses/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        return expenseRepository.findById(id).map(expense -> {
            FamilyMember responsable = familyRepository.findById(request.getIdFamily())
                    .orElseThrow(() -> new RuntimeException("Responsable no encontrado"));

            expense.setTitle(request.getTitle());
            expense.setDescription(request.getDescription());
            expense.setPeriod(request.getPeriod());
            expense.setResponsible(responsable);
            expense.setValue(BigDecimal.valueOf(request.getValue()));

            expenseRepository.save(expense);

            return ResponseEntity.ok(new ApiResponse("Gasto actualizado correctamente", id.toString()));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE: api/v1/rest/expenses/{id}
    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        return expenseRepository.findById(id).map(expense -> {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse("Gasto eliminado correctamente", id.toString()));
        }).orElse(ResponseEntity.notFound().build());
    }

    public static class ExpenseRequest {
        private String title;
        private String description;
        private String period;
        private UUID idFamily;
        private Double value;

        // Getters y Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public UUID getIdFamily() { return idFamily; }
        public void setIdFamily(UUID idFamily) { this.idFamily = idFamily; }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
    }

    public static class ApiResponse {
        private String mensaje;
        private String idExpense;

        public ApiResponse(String mensaje) {
            this.mensaje = mensaje;
        }

        public ApiResponse(String mensaje, String idExpense) {
            this.mensaje = mensaje;
            this.idExpense = idExpense;
        }

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }

        public String getIdExpense() { return idExpense; }
        public void setIdExpense(String idExpense) { this.idExpense = idExpense; }
    }
}

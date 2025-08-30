package com.familyspencesapi.controllers;

import com.familyspencesapi.domain.expenseControl.Expense;
import com.familyspencesapi.domain.family.FamilyMember;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/rest/expenses")
@CrossOrigin(origins = "*")
public class ExpenseControlController {

    // Datos quemados - Miembros de familia
    private static final Map<UUID, FamilyMember> FAMILY_MEMBERS = new ConcurrentHashMap<>();

    // Datos quemados - Gastos
    private static final Map<UUID, Expense> EXPENSES = new ConcurrentHashMap<>();

    static {
        // Inicializar miembros de familia
        UUID familyId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        UUID familyId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        UUID familyId3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

        FAMILY_MEMBERS.put(familyId1, new FamilyMember(familyId1, "Juan Pérez", "juan.perez@email.com", "Padre"));
        FAMILY_MEMBERS.put(familyId2, new FamilyMember(familyId2, "María García", "maria.garcia@email.com", "Madre"));
        FAMILY_MEMBERS.put(familyId3, new FamilyMember(familyId3, "Ana Pérez", "ana.perez@email.com", "Hija"));

        // Inicializar gastos de ejemplo
        UUID expenseId1 = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
        UUID expenseId2 = UUID.fromString("660e8400-e29b-41d4-a716-446655440002");
        UUID expenseId3 = UUID.fromString("660e8400-e29b-41d4-a716-446655440003");

        EXPENSES.put(expenseId1, new Expense(
                expenseId1, "Supermercado", "Compras mensuales del hogar", "Mensual",
                FAMILY_MEMBERS.get(familyId1), new BigDecimal("450.75")
        ));

        EXPENSES.put(expenseId2, new Expense(
                expenseId2, "Gasolina", "Combustible para el vehículo", "Semanal",
                FAMILY_MEMBERS.get(familyId1), new BigDecimal("80.00")
        ));

        EXPENSES.put(expenseId3, new Expense(
                expenseId3, "Material escolar", "Útiles para el colegio", "Trimestral",
                FAMILY_MEMBERS.get(familyId2), new BigDecimal("120.50")
        ));
    }

    // GET: api/v1/rest/expenses
    @GetMapping("")
    public ResponseEntity<List<Expense>> getAll() {
        List<Expense> expenses = new ArrayList<>(EXPENSES.values());
        return ResponseEntity.ok(expenses);
    }

    // GET: api/v1/rest/expenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable UUID id) {
        Expense expense = EXPENSES.get(id);
        if (expense != null) {
            return ResponseEntity.ok(expense);
        }
        return ResponseEntity.notFound().build();
    }

    // POST: api/v1/rest/expenses
    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody ExpenseRequest request) {
        try {
            FamilyMember responsible = FAMILY_MEMBERS.get(request.getIdFamily());
            if (responsible == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error: Miembro de familia no encontrado con ID: " + request.getIdFamily()));
            }

            UUID newId = UUID.randomUUID();
            Expense expense = new Expense(
                    newId,
                    request.getTitle().trim(),
                    request.getDescription() != null ? request.getDescription().trim() : "",
                    request.getPeriod().trim(),
                    responsible,
                    request.getValue()
            );

            EXPENSES.put(newId, expense);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Gasto registrado correctamente", newId.toString()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    // PUT: api/v1/rest/expenses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request) {
        try {
            Expense expense = EXPENSES.get(id);
            if (expense == null) {
                return ResponseEntity.notFound().build();
            }

            FamilyMember responsible = FAMILY_MEMBERS.get(request.getIdFamily());
            if (responsible == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error: Miembro de familia no encontrado con ID: " + request.getIdFamily()));
            }

            // Actualizar el gasto existente
            expense.setTitle(request.getTitle().trim());
            expense.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
            expense.setPeriod(request.getPeriod().trim());
            expense.setResponsible(responsible);
            expense.setValue(request.getValue());
            expense.setUpdatedAt(LocalDateTime.now());

            return ResponseEntity.ok(new ApiResponse("Gasto actualizado correctamente", id.toString()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    // DELETE: api/v1/rest/expenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            Expense expense = EXPENSES.remove(id);
            if (expense != null) {
                return ResponseEntity.ok(new ApiResponse("Gasto eliminado correctamente", id.toString()));
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    // GET: api/v1/rest/expenses/family-members (endpoint adicional para obtener miembros)
    @GetMapping("/family-members")
    public ResponseEntity<List<FamilyMember>> getFamilyMembers() {
        List<FamilyMember> members = new ArrayList<>(FAMILY_MEMBERS.values());
        return ResponseEntity.ok(members);
    }

    // DTO para requests con validaciones
    public static class ExpenseRequest {
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
        private String title;

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        private String description;

        @NotBlank(message = "El período es obligatorio")
        @Size(min = 2, max = 50, message = "El período debe tener entre 2 y 50 caracteres")
        private String period;

        @NotNull(message = "El ID del miembro de familia es obligatorio")
        private UUID idFamily;

        @NotNull(message = "El valor es obligatorio")
        @Positive(message = "El valor debe ser mayor que cero")
        private BigDecimal value;

        // Constructores
        public ExpenseRequest() {}

        public ExpenseRequest(String title, String description, String period, UUID idFamily, BigDecimal value) {
            this.title = title;
            this.description = description;
            this.period = period;
            this.idFamily = idFamily;
            this.value = value;
        }

        // Getters y Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public UUID getIdFamily() { return idFamily; }
        public void setIdFamily(UUID idFamily) { this.idFamily = idFamily; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }

    // DTO para responses
    public static class ApiResponse {
        private String mensaje;
        private String idExpense;
        private long timestamp;

        public ApiResponse(String mensaje) {
            this.mensaje = mensaje;
            this.timestamp = System.currentTimeMillis();
        }

        public ApiResponse(String mensaje, String idExpense) {
            this.mensaje = mensaje;
            this.idExpense = idExpense;
            this.timestamp = System.currentTimeMillis();
        }

        // Getters y Setters
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }

        public String getIdExpense() { return idExpense; }
        public void setIdExpense(String idExpense) { this.idExpense = idExpense; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
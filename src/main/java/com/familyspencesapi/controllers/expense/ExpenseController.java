package com.familyspencesapi.controllers.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.family.FamilyMember;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rest/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

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

        // Inicializar gastos de ejemplo con categorías
        UUID expenseId1 = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
        UUID expenseId2 = UUID.fromString("660e8400-e29b-41d4-a716-446655440002");
        UUID expenseId3 = UUID.fromString("660e8400-e29b-41d4-a716-446655440003");

        EXPENSES.put(expenseId1, new Expense(
                expenseId1, "Supermercado", "Compras mensuales del hogar", "enero",
                FAMILY_MEMBERS.get(familyId1), new BigDecimal("450.75"), ExpenseCategory.ALIMENTACION
        ));

        EXPENSES.put(expenseId2, new Expense(
                expenseId2, "Gasolina", "Combustible para el vehículo", "enero",
                FAMILY_MEMBERS.get(familyId1), new BigDecimal("80.00"), ExpenseCategory.TRANSPORTE
        ));

        EXPENSES.put(expenseId3, new Expense(
                expenseId3, "Material escolar", "Útiles para el colegio", "enero",
                FAMILY_MEMBERS.get(familyId2), new BigDecimal("120.50"), ExpenseCategory.EDUCACION
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

    // GET: api/v1/rest/expenses/by-category/{category}
    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<Expense>> getByCategory(@PathVariable ExpenseCategory category) {
        List<Expense> expensesByCategory = EXPENSES.values().stream()
                .filter(expense -> expense.getCategory() == category)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expensesByCategory);
    }

    // GET: api/v1/rest/expenses/by-period/{period}
    @GetMapping("/by-period/{period}")
    public ResponseEntity<List<Expense>> getByPeriod(@PathVariable String period) {
        List<Expense> expensesByPeriod = EXPENSES.values().stream()
                .filter(expense -> expense.isSamePeriod(period))
                .collect(Collectors.toList());
        return ResponseEntity.ok(expensesByPeriod);
    }

    // GET: api/v1/rest/expenses/expensive
    @GetMapping("/expensive")
    public ResponseEntity<List<Expense>> getExpensiveExpenses() {
        List<Expense> expensiveExpenses = EXPENSES.values().stream()
                .filter(Expense::isExpensive)
                .collect(Collectors.toList());
        return ResponseEntity.ok(expensiveExpenses);
    }

    // GET: api/v1/rest/expenses/total-by-period/{period}
    @GetMapping("/total-by-period/{period}")
    public ResponseEntity<TotalResponse> getTotalByPeriod(@PathVariable String period) {
        BigDecimal total = EXPENSES.values().stream()
                .filter(expense -> expense.isSamePeriod(period))
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(new TotalResponse(period, total));
    }

    // POST: api/v1/rest/expenses
    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody ExpenseRequest request, BindingResult result) {
        try {
            // Validar errores de binding
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Errores de validación: " + String.join(", ", errors)));
            }

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
                    request.getValue(),
                    request.getCategory()
            );

            // Validar período personalizado
            if (!expense.isValidPeriod()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error: El período '" + request.getPeriod() + "' no es válido"));
            }

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
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request, BindingResult result) {
        try {
            // Validar errores de binding
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Errores de validación: " + String.join(", ", errors)));
            }

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
            expense.setCategory(request.getCategory());

            // Validar período personalizado
            if (!expense.isValidPeriod()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error: El período '" + request.getPeriod() + "' no es válido"));
            }

            expense.updateTimestamp(); // Actualizar timestamp manualmente

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

    // GET: api/v1/rest/expenses/family-members
    @GetMapping("/family-members")
    public ResponseEntity<List<FamilyMember>> getFamilyMembers() {
        List<FamilyMember> members = new ArrayList<>(FAMILY_MEMBERS.values());
        return ResponseEntity.ok(members);
    }

    // GET: api/v1/rest/expenses/categories
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryInfo>> getCategories() {
        List<CategoryInfo> categories = Arrays.stream(ExpenseCategory.values())
                .map(category -> new CategoryInfo(category.name(), category.getDisplayName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    // DTO para requests con validaciones mejoradas
    public static class ExpenseRequest {
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
        private String title;

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        private String description;

        @NotBlank(message = "El período es obligatorio")
        @Pattern(regexp = "^(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre|\\d{4}-\\d{2})$",
                message = "El período debe ser un mes válido en español o formato YYYY-MM")
        private String period;

        @NotNull(message = "El ID del miembro de familia es obligatorio")
        private UUID idFamily;

        @NotNull(message = "El valor es obligatorio")
        @DecimalMin(value = "0.01", message = "El valor debe ser mayor que 0")
        @DecimalMax(value = "999999999.99", message = "El valor excede el límite permitido")
        @Digits(integer = 9, fraction = 2, message = "Formato de valor inválido")
        private BigDecimal value;

        @NotNull(message = "La categoría es obligatoria")
        private ExpenseCategory category;

        // Constructores
        public ExpenseRequest() {}

        public ExpenseRequest(String title, String description, String period,
                              UUID idFamily, BigDecimal value, ExpenseCategory category) {
            this.title = title;
            this.description = description;
            this.period = period;
            this.idFamily = idFamily;
            this.value = value;
            this.category = category;
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

        public ExpenseCategory getCategory() { return category; }
        public void setCategory(ExpenseCategory category) { this.category = category; }
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

    // DTO para información de categorías
    public static class CategoryInfo {
        private String code;
        private String displayName;

        public CategoryInfo(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        // Getters y Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
    }

    // DTO para totales
    public static class TotalResponse {
        private String period;
        private BigDecimal total;
        private String formattedTotal;

        public TotalResponse(String period, BigDecimal total) {
            this.period = period;
            this.total = total;
            this.formattedTotal = String.format("$%.2f", total);
        }

        // Getters y Setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }

        public String getFormattedTotal() { return formattedTotal; }
        public void setFormattedTotal(String formattedTotal) { this.formattedTotal = formattedTotal; }
    }
}
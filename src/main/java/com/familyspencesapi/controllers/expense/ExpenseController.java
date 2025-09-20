package com.familyspencesapi.controllers.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.service.expense.ExpenseService;
import com.familyspencesapi.service.users.RegisterUserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rest/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final RegisterUserService userService;

    // Constructor injection
    public ExpenseController(ExpenseService expenseService, RegisterUserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    // GET: api/v1/rest/expenses
    @GetMapping("")
    public ResponseEntity<List<Expense>> getAll() {
        try {
            List<Expense> expenses = expenseService.findAll();
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable UUID id) {
        try {
            Optional<Expense> expense = expenseService.findById(id);
            return expense.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/by-category/{category}
    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<Expense>> getByCategory(@PathVariable ExpenseCategory category) {
        try {
            List<Expense> expenses = expenseService.findByCategory(category);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/by-period/{period}
    @GetMapping("/by-period/{period}")
    public ResponseEntity<List<Expense>> getByPeriod(@PathVariable String period) {
        try {
            List<Expense> expenses = expenseService.findByPeriod(period);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/by-family/{familyId}
    @GetMapping("/by-family/{familyId}")
    public ResponseEntity<List<Expense>> getByFamily(@PathVariable UUID familyId) {
        try {
            List<Expense> expenses = expenseService.findByResponsibleFamilyId(familyId);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/by-user/{userId}
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Expense>> getByUser(@PathVariable UUID userId) {
        try {
            List<Expense> expenses = expenseService.findByResponsibleId(userId);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/expensive
    @GetMapping("/expensive")
    public ResponseEntity<List<Expense>> getExpensiveExpenses() {
        try {
            List<Expense> expenses = expenseService.findExpensiveExpenses();
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/total-by-period/{period}
    @GetMapping("/total-by-period/{period}")
    public ResponseEntity<TotalResponse> getTotalByPeriod(@PathVariable String period) {
        try {
            BigDecimal total = expenseService.calculateTotalByPeriod(period);
            return ResponseEntity.ok(new TotalResponse(period, total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/total-by-family/{familyId}/{period}
    @GetMapping("/total-by-family/{familyId}/{period}")
    public ResponseEntity<TotalResponse> getTotalByFamilyAndPeriod(
            @PathVariable UUID familyId, @PathVariable String period) {
        try {
            BigDecimal total = expenseService.calculateTotalByFamilyAndPeriod(familyId, period);
            return ResponseEntity.ok(new TotalResponse(period, total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/statistics
    @GetMapping("/statistics")
    public ResponseEntity<ExpenseService.ExpenseStatistics> getStatistics() {
        try {
            ExpenseService.ExpenseStatistics stats = expenseService.getExpenseStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

            // Verificar que el usuario existe
            Optional<RegisterUser> userOpt = userService.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error: Usuario no encontrado con ID: " + request.getUserId()));
            }

            RegisterUser responsible = userOpt.get();
            Expense expense = new Expense(
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

            Expense savedExpense = expenseService.save(expense);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Gasto registrado correctamente", savedExpense.getId().toString()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Error de validación: " + e.getMessage()));
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

            Optional<Expense> expenseOpt = expenseService.findById(id);
            if (expenseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<RegisterUser> userOpt = userService.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error: Usuario no encontrado con ID: " + request.getUserId()));
            }

            Expense expense = expenseOpt.get();
            RegisterUser responsible = userOpt.get();

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

            expenseService.save(expense);

            return ResponseEntity.ok(new ApiResponse("Gasto actualizado correctamente", id.toString()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Error de validación: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    // DELETE: api/v1/rest/expenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            boolean deleted = expenseService.deleteById(id);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse("Gasto eliminado correctamente", id.toString()));
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error interno del servidor: " + e.getMessage()));
        }
    }

    // GET: api/v1/rest/expenses/users
    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getUsers() {
        try {
            List<RegisterUser> users = userService.findAll();
            List<UserInfo> userInfos = users.stream()
                    .map(user -> new UserInfo(
                            user.getId(),
                            user.getfullName(),
                            user.getEmail(),
                            user.getRelationship() != null ? user.getRelationship().getType() : "Sin relación",
                            user.getFamilyId()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userInfos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/users/by-family/{familyId}
    @GetMapping("/users/by-family/{familyId}")
    public ResponseEntity<List<UserInfo>> getUsersByFamily(@PathVariable UUID familyId) {
        try {
            List<RegisterUser> users = userService.findByFamilyId(familyId);
            List<UserInfo> userInfos = users.stream()
                    .map(user -> new UserInfo(
                            user.getId(),
                            user.getfullName(),
                            user.getEmail(),
                            user.getRelationship() != null ? user.getRelationship().getType() : "Sin relación",
                            user.getFamilyId()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userInfos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/categories
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryInfo>> getCategories() {
        try {
            List<CategoryInfo> categories = Arrays.stream(ExpenseCategory.values())
                    .map(category -> new CategoryInfo(category.name(), category.getDisplayName()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/recent/{days}
    @GetMapping("/recent/{days}")
    public ResponseEntity<List<Expense>> getRecentExpenses(@PathVariable int days) {
        try {
            List<Expense> expenses = expenseService.getRecentExpenses(days);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/current-month
    @GetMapping("/current-month")
    public ResponseEntity<List<Expense>> getCurrentMonthExpenses() {
        try {
            List<Expense> expenses = expenseService.getCurrentMonthExpenses();
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET: api/v1/rest/expenses/top/{limit}
    @GetMapping("/top/{limit}")
    public ResponseEntity<List<Expense>> getTopExpenses(@PathVariable int limit) {
        try {
            List<Expense> expenses = expenseService.getTopExpensesByValue(limit);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

        @NotNull(message = "El ID del usuario es obligatorio")
        private UUID userId;

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
                              UUID userId, BigDecimal value, ExpenseCategory category) {
            this.title = title;
            this.description = description;
            this.period = period;
            this.userId = userId;
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

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }

        public ExpenseCategory getCategory() { return category; }
        public void setCategory(ExpenseCategory category) { this.category = category; }
    }

    // DTO para información de usuarios
    public static class UserInfo {
        private UUID id;
        private String fullName;
        private String email;
        private String relationship;
        private UUID familyId;

        public UserInfo(UUID id, String fullName, String email, String relationship, UUID familyId) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.relationship = relationship;
            this.familyId = familyId;
        }

        // Getters y Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }

        public UUID getFamilyId() { return familyId; }
        public void setFamilyId(UUID familyId) { this.familyId = familyId; }
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
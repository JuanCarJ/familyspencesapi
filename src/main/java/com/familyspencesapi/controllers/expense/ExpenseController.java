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

@RestController
@RequestMapping("/api/v1/rest/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {

    // Constantes para mensajes y validaciones
    private static final String ERROR_VALIDATION_MESSAGE = "Errores de validación: ";
    private static final String ERROR_USER_NOT_FOUND = "Error: Usuario no encontrado con ID: ";
    private static final String ERROR_INVALID_PERIOD = "Error: El período '%s' no es válido";
    private static final String SUCCESS_EXPENSE_CREATED = "Gasto registrado correctamente";
    private static final String SUCCESS_EXPENSE_UPDATED = "Gasto actualizado correctamente";
    private static final String SUCCESS_EXPENSE_DELETED = "Gasto eliminado correctamente";
    private static final String ERROR_VALIDATION_PREFIX = "Error de validación: ";
    private static final String ERROR_INTERNAL_SERVER = "Error interno del servidor: ";
    private static final String DEFAULT_RELATIONSHIP = "Sin relación";

    private final ExpenseService expenseService;
    private final RegisterUserService userService;

    public ExpenseController(ExpenseService expenseService, RegisterUserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }


    @GetMapping("")
    public ResponseEntity<List<Expense>> getAll() {
        try {
            System.out.println("=== INICIO getAll() ===");

            System.out.println("Llamando a expenseService.findAll()...");
            List<Expense> expenses = expenseService.findAll();

            System.out.println("Número de gastos encontrados: " + expenses.size());

            // Verificar cada expense individualmente
            for (int i = 0; i < expenses.size(); i++) {
                Expense expense = expenses.get(i);
                try {
                    System.out.println("Procesando expense " + i + " - ID: " + expense.getId());
                    System.out.println("Responsible ID: " + expense.getResponsible().getId());
                    System.out.println("Responsible Name: " + expense.getResponsible().getfullName());
                } catch (Exception e) {
                    System.out.println("ERROR en expense " + i + ": " + e.getMessage());
                    e.printStackTrace();
                    // Remover el expense problemático de la lista
                    expenses.remove(i);
                    i--; // Ajustar el índice
                }
            }

            System.out.println("=== FIN getAll() - SUCCESS ===");
            return ResponseEntity.ok(expenses);

        } catch (Exception e) {
            System.out.println("=== ERROR GENERAL en getAll() ===");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable UUID id) {
        try {
            Optional<Expense> expense = expenseService.findById(id);
            if (expense.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(expense.get()); // 200 explícito
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 explícito
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 explícito
        }
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<Expense>> getByCategory(@PathVariable ExpenseCategory category) {
        try {
            List<Expense> expenses = expenseService.findByCategory(category);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-period/{period}")
    public ResponseEntity<List<Expense>> getByPeriod(@PathVariable String period) {
        try {
            List<Expense> expenses = expenseService.findByPeriod(period);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-family/{familyId}")
    public ResponseEntity<List<Expense>> getByFamily(@PathVariable UUID familyId) {
        try {
            List<Expense> expenses = expenseService.findByResponsibleFamilyId(familyId);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Expense>> getByUser(@PathVariable UUID userId) {
        try {
            List<Expense> expenses = expenseService.findByResponsibleId(userId);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expensive")
    public ResponseEntity<List<Expense>> getExpensiveExpenses() {
        try {
            List<Expense> expenses = expenseService.findExpensiveExpenses();
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/total-by-period/{period}")
    public ResponseEntity<TotalResponse> getTotalByPeriod(@PathVariable String period) {
        try {
            BigDecimal total = expenseService.calculateTotalByPeriod(period);
            return ResponseEntity.ok(new TotalResponse(period, total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @GetMapping("/statistics")
    public ResponseEntity<ExpenseService.ExpenseStatistics> getStatistics() {
        try {
            ExpenseService.ExpenseStatistics stats = expenseService.getExpenseStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ExpenseRequest request, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_VALIDATION_MESSAGE + String.join(", ", errors)));
            }

            Optional<RegisterUser> userOpt = userService.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_USER_NOT_FOUND + request.getUserId()));
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

            if (!expense.isValidPeriod()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(String.format(ERROR_INVALID_PERIOD, request.getPeriod())));
            }

            Expense savedExpense = expenseService.save(expense);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(SUCCESS_EXPENSE_CREATED, savedExpense.getId().toString()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_VALIDATION_PREFIX + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(ERROR_INTERNAL_SERVER + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable UUID id, @Valid @RequestBody ExpenseRequest request, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(error -> error.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_VALIDATION_MESSAGE + String.join(", ", errors)));
            }

            Optional<Expense> expenseOpt = expenseService.findById(id);
            if (expenseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<RegisterUser> userOpt = userService.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(ERROR_USER_NOT_FOUND + request.getUserId()));
            }

            Expense expense = expenseOpt.get();
            RegisterUser responsible = userOpt.get();

            expense.setTitle(request.getTitle().trim());
            expense.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
            expense.setPeriod(request.getPeriod().trim());
            expense.setResponsible(responsible);
            expense.setValue(request.getValue());
            expense.setCategory(request.getCategory());

            if (!expense.isValidPeriod()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(String.format(ERROR_INVALID_PERIOD, request.getPeriod())));
            }

            expenseService.save(expense);

            return ResponseEntity.ok(new ApiResponse(SUCCESS_EXPENSE_UPDATED, id.toString()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(ERROR_VALIDATION_PREFIX + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(ERROR_INTERNAL_SERVER + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        try {
            boolean deleted = expenseService.deleteById(id);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse(SUCCESS_EXPENSE_DELETED, id.toString()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Gasto no encontrado con ID: " + id));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(ERROR_INTERNAL_SERVER + e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfo>> getUsers() {
        try {
            List<RegisterUser> users = userService.findAll();
            List<UserInfo> userInfos = users.stream()
                    .map(user -> new UserInfo(
                            user.getId(),
                            user.getfullName(),
                            user.getEmail(),
                            user.getRelationship() != null ? user.getRelationship().getType() : DEFAULT_RELATIONSHIP,
                            user.getFamilyId()
                    ))
                    .toList();
            return ResponseEntity.ok(userInfos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/by-family/{familyId}")
    public ResponseEntity<List<UserInfo>> getUsersByFamily(@PathVariable UUID familyId) {
        try {
            List<RegisterUser> users = userService.findByFamilyId(familyId);
            List<UserInfo> userInfos = users.stream()
                    .map(user -> new UserInfo(
                            user.getId(),
                            user.getfullName(),
                            user.getEmail(),
                            user.getRelationship() != null ? user.getRelationship().getType() : DEFAULT_RELATIONSHIP,
                            user.getFamilyId()
                    ))
                    .toList();
            return ResponseEntity.ok(userInfos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryInfo>> getCategories() {
        try {
            List<CategoryInfo> categories = Arrays.stream(ExpenseCategory.values())
                    .map(category -> new CategoryInfo(category.name(), category.getDisplayName()))
                    .toList();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/recent/{days}")
    public ResponseEntity<List<Expense>> getRecentExpenses(@PathVariable int days) {
        try {
            List<Expense> expenses = expenseService.getRecentExpenses(days);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/current-month")
    public ResponseEntity<List<Expense>> getCurrentMonthExpenses() {
        try {
            List<Expense> expenses = expenseService.getCurrentMonthExpenses();
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top/{limit}")
    public ResponseEntity<List<Expense>> getTopExpenses(@PathVariable int limit) {
        try {
            List<Expense> expenses = expenseService.getTopExpensesByValue(limit);
            return ResponseEntity.ok(expenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }

        public String getIdExpense() { return idExpense; }
        public void setIdExpense(String idExpense) { this.idExpense = idExpense; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class CategoryInfo {
        private String code;
        private String displayName;

        public CategoryInfo(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
    }

    public static class TotalResponse {
        private String period;
        private BigDecimal total;
        private String formattedTotal;

        public TotalResponse(String period, BigDecimal total) {
            this.period = period;
            this.total = total;
            this.formattedTotal = String.format("$%.2f", total);
        }

        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }

        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }

        public String getFormattedTotal() { return formattedTotal; }
        public void setFormattedTotal(String formattedTotal) { this.formattedTotal = formattedTotal; }
    }
}
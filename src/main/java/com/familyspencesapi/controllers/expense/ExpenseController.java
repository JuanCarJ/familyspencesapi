package com.familyspencesapi.controllers.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.service.expense.ExpenseService;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/rest/expenses")

public class ExpenseController {

    private final ExpenseService expenseService;
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }


    @GetMapping("")
    public ResponseEntity<List<Expense>> getAll() {
        try {
            List<Expense> expenses = expenseService.findAll();
            return ResponseEntity.ok(expenses);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable UUID id) {
        try {
            Expense expense = expenseService.findById(id);
            if (expense == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(expense);
            }
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

    @GetMapping("/by-user/{userMail}")
    public ResponseEntity<List<Expense>> getByUser(@PathVariable String userMail) {
        try {
            List<Expense> expenses = expenseService.findByResponsibleId(userMail);
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

    @PostMapping("/{familyId}/{mail}")
    public ResponseEntity<ApiResponse> create(@PathVariable UUID familyId,@PathVariable String mail,@Valid @RequestBody ExpenseRequest request, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(Messages.ERROR_VALIDATION_MESSAGE + String.join(", ", errors)));
            }

            String savedExpense = expenseService.save(request,mail,familyId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(Messages.SUCCESS_EXPENSE_CREATED, savedExpense ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(Messages.ERROR_VALIDATION_PREFIX + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(Messages.ERROR_INTERNAL_SERVER + e.getMessage()));
        }
    }

    @PutMapping("/{mail}/{idExpense}")
    public ResponseEntity<ApiResponse> update(@PathVariable String mail,@PathVariable UUID idExpense, @Valid @RequestBody ExpenseRequest request, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(Messages.ERROR_VALIDATION_MESSAGE + String.join(", ", errors)));
            }

            var message = expenseService.updateExpense(request,mail,idExpense);

            return ResponseEntity.ok(new ApiResponse(message, idExpense.toString()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(Messages.ERROR_VALIDATION_PREFIX + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(Messages.ERROR_INTERNAL_SERVER + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable UUID id) {
        try {
            boolean deleted = expenseService.deleteById(id);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse(Messages.SUCCESS_EXPENSE_DELETED, id.toString()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Gasto no encontrado con ID: " + id));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(Messages.ERROR_INTERNAL_SERVER + e.getMessage()));
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

}
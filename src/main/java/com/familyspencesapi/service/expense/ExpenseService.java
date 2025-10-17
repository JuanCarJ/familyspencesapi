package com.familyspencesapi.service.expense;

import com.familyspencesapi.config.messages.budgetprocessor.BudgetProcessQueueConfig;
import com.familyspencesapi.controllers.expense.ExpenseRequest;
import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.messages.expense.MessageSenderBrokerExpense;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ExpenseService {


    private final ExpenseRepository expenseRepository;
    private final RegisterUserRepository registerUserRepository;
    private final MessageSenderBrokerExpense messageSenderBrokerExpense;
    private final BudgetProcessQueueConfig processQueueConfig;

    // Constructor injection
    public ExpenseService(ExpenseRepository expenseRepository, RegisterUserRepository registerUserRepository, MessageSenderBrokerExpense messageSenderBrokerExpense, BudgetProcessQueueConfig processQueueConfig) {
        this.expenseRepository = expenseRepository;
        this.registerUserRepository = registerUserRepository;
        this.messageSenderBrokerExpense = messageSenderBrokerExpense;
        this.processQueueConfig = processQueueConfig;
    }

    /**
     * Encontrar todos los gastos
     */
    @Transactional(readOnly = true)
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    /**
     * Encontrar gasto por ID
     */
    @Transactional(readOnly = true)
    public Expense findById(UUID id) {
        return expenseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Expense not found"));
    }

    /**
     * Guardar gasto
     */
    public String save(ExpenseRequest request, String userMail, UUID familyId) {
        Optional<RegisterUser> userOpt = registerUserRepository.findByEmail(userMail);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Responsible not found");
        }

        Expense expense = new Expense(
                request.getTitle().trim(),
                request.getDescription() != null ? request.getDescription().trim() : "",
                request.getPeriod().trim(),
                userMail,
                request.getValue(),
                request.getCategory(),
                familyId
        );

        if (!expense.isValidPeriod()) {
            throw new IllegalArgumentException("Invalid period");
        }

        messageSenderBrokerExpense.execute(expense, processQueueConfig.getRoutingKeyCreate());

        return "The message was sent successfully.";
    }

    /**
     * Eliminar gasto por ID
     */
    public boolean deleteById(UUID id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Encontrar gastos por categoría
     */
    @Transactional(readOnly = true)
    public List<Expense> findByCategory(ExpenseCategory category) {
        return expenseRepository.findByCategory(category);
    }

    /**
     * Encontrar gastos por período
     */
    @Transactional(readOnly = true)
    public List<Expense> findByPeriod(String period) {
        return expenseRepository.findByPeriodIgnoreCase(period);
    }

    /**
     * Encontrar gastos por responsable (usuario)
     */
    @Transactional(readOnly = true)
    public List<Expense> findByResponsibleId(String responsibleMail) {
        return expenseRepository.findByResponsible(responsibleMail);
    }

    /**
     * Encontrar gastos por familia
     */
    @Transactional(readOnly = true)
    public List<Expense> findByResponsibleFamilyId(UUID familyId) {
        return expenseRepository.findByResponsibleFamilyId(familyId);
    }

    /**
     * Encontrar gastos caros
     */
    @Transactional(readOnly = true)
    public List<Expense> findExpensiveExpenses() {
        return expenseRepository.findExpensiveExpenses();
    }

    /**
     * Calcular total de gastos por período
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByPeriod(String period) {
        return expenseRepository.calculateTotalByPeriod(period);
    }

    /**
     * Calcular total de gastos por categoría
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByCategory(ExpenseCategory category) {
        return expenseRepository.calculateTotalByCategory(category);
    }

    /**
     * Calcular total de gastos por familia y período
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByFamilyAndPeriod(UUID familyId, String period) {
        return expenseRepository.calculateTotalByFamilyAndPeriod(familyId, period);
    }

    /**
     * Obtener estadísticas generales de gastos
     */
    @Transactional(readOnly = true)
    public ExpenseStatistics getExpenseStatistics() {
        Object[] basicStats = expenseRepository.getBasicStatistics();

        // Encontrar la categoría más cara
        List<Object[]> categoryTotals = expenseRepository.findCategoryTotals();
        String mostExpensiveCategoryName = "N/A";

        if (!categoryTotals.isEmpty()) {
            ExpenseCategory topCategory = (ExpenseCategory) categoryTotals.getFirst()[0];
            mostExpensiveCategoryName = topCategory.getDisplayName();
        }

        return new ExpenseStatistics(basicStats, mostExpensiveCategoryName);
    }

    /**
     * Actualizar un gasto existente
     */
    public Expense updateExpense(ExpenseRequest request,String responsiblemail, UUID expenseId) {

        // Verificar que el gasto existe
        Optional<Expense> expenseOpt = expenseRepository.findById(expenseId);
        if (expenseOpt.isEmpty()) {
            throw new IllegalArgumentException("Gasto no encontrado");
        }

        // Verificar que el responsable existe
        Optional<RegisterUser> responsible = registerUserRepository.findByEmail(responsiblemail);
        if (responsible.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Expense expense = expenseOpt.get();
        expense.setTitle(request.getTitle().trim());
        expense.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        expense.setPeriod(request.getPeriod().trim());
        expense.setResponsible(responsiblemail);
        expense.setValue(request.getValue());
        expense.setCategory(request.getCategory());

        if (!expense.isValidPeriod()) {
            throw new IllegalArgumentException("Período inválido: " + expense.getPeriod());
        }

        return expenseRepository.save(expense);
    }

    /**
     * Eliminar un gasto
     */
    public boolean deleteExpense(UUID expenseId) {
        if (expenseRepository.existsById(expenseId)) {
            expenseRepository.deleteById(expenseId);
            return true;
        }
        return false;
    }

    /**
     * Obtener gastos recientes (últimos N días)
     */
    @Transactional(readOnly = true)
    public List<Expense> getRecentExpenses(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return expenseRepository.findRecentExpenses(since);
    }

    /**
     * Obtener gastos del mes actual
     */
    @Transactional(readOnly = true)
    public List<Expense> getCurrentMonthExpenses() {
        return expenseRepository.findCurrentMonthExpenses();
    }

    /**
     * Obtener top gastos por valor
     */
    @Transactional(readOnly = true)
    public List<Expense> getTopExpensesByValue(int limit) {
        return expenseRepository.findTopExpensesByValue(
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }

    /**
     * Verificar si un usuario tiene gastos asociados
     */

    /**
     * Obtener gastos por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDateRange(LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Calcular promedio de gastos por categoría
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateAverageByCategory(ExpenseCategory category) {
        List<Expense> expenses = expenseRepository.findByCategory(category);
        if (expenses.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = expenses.stream()
                .map(Expense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Obtener conteo de gastos por categoría
     */
    @Transactional(readOnly = true)
    public List<Object[]> getExpenseCountByCategory() {
        return expenseRepository.countExpensesByCategory();
    }

    /**
     * Clase interna para estadísticas de gastos
     */
    public static class ExpenseStatistics {
        private final long totalExpenses;
        private final BigDecimal totalAmount;
        private final BigDecimal averageAmount;
        private final String mostExpensiveCategory;

        public ExpenseStatistics(Object[] basicStats, String mostExpensiveCategory) {
            this.totalExpenses = ((Number) basicStats[0]).longValue();
            this.totalAmount = basicStats[1] != null ? (BigDecimal) basicStats[1] : BigDecimal.ZERO;
            this.averageAmount = basicStats[2] != null ?
                    ((BigDecimal) basicStats[2]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            this.mostExpensiveCategory = mostExpensiveCategory;
        }

        public long getTotalExpenses() {
            return totalExpenses;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getAverageAmount() {
            return averageAmount;
        }

        public String getMostExpensiveCategory() {
            return mostExpensiveCategory;
        }
    }
}
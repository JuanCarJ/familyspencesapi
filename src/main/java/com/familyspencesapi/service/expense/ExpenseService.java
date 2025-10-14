package com.familyspencesapi.service.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;
    private final RegisterUserRepository registerUserRepository;

    // Constructor injection
    public ExpenseService(ExpenseRepository expenseRepository, RegisterUserRepository registerUserRepository) {
        this.expenseRepository = expenseRepository;
        this.registerUserRepository = registerUserRepository;
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
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
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
    public List<Expense> findByResponsibleId(UUID userId) {
        return expenseRepository.findByResponsibleId(userId);
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
     * Calcular total de gastos por responsable
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByResponsible(UUID responsibleId) {
        Optional<RegisterUser> responsible = registerUserRepository.findById(responsibleId);
        if (responsible.isPresent()) {
            return expenseRepository.calculateTotalByResponsible(responsible.get());
        }
        return BigDecimal.ZERO;
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
     * Buscar gastos con múltiples filtros
     */
    @Transactional(readOnly = true)
    public List<Expense> searchExpenses(String title, String period, ExpenseCategory category,
                                        UUID responsibleId, Pageable pageable) {
        RegisterUser responsible = null;
        if (responsibleId != null) {
            Optional<RegisterUser> responsibleOpt = registerUserRepository.findById(responsibleId);
            responsible = responsibleOpt.orElse(null);
        }

        return expenseRepository.findExpensesByCriteria(title, period, category, responsible, pageable)
                .getContent();
    }

    /**
     * Crear un nuevo gasto con validaciones adicionales
     */
    public Expense createExpense(String title, String description, String period,
                                 UUID responsibleId, BigDecimal value, ExpenseCategory category) {

        // Verificar que el responsable existe
        Optional<RegisterUser> responsible = registerUserRepository.findById(responsibleId);
        if (responsible.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Crear el gasto
        Expense expense = new Expense(title, description, period, responsible.get(), value, category);

        // Validar período
        if (!expense.isValidPeriod()) {
            throw new IllegalArgumentException("Período inválido: " + period);
        }

        // Verificar si ya existe un gasto similar
        boolean exists = expenseRepository.existsSimilarExpense(
                title, period, responsible.get(), UUID.randomUUID()
        );

        if (exists) {
            logger.warn("Ya existe un gasto similar para el título: {}, período: {}", title, period);
        }

        return expenseRepository.save(expense);
    }

    /**
     * Actualizar un gasto existente
     */
    public Expense updateExpense(UUID expenseId, String title, String description, String period,
                                 UUID responsibleId, BigDecimal value, ExpenseCategory category) {

        // Verificar que el gasto existe
        Optional<Expense> expenseOpt = expenseRepository.findById(expenseId);
        if (expenseOpt.isEmpty()) {
            throw new IllegalArgumentException("Gasto no encontrado");
        }

        // Verificar que el responsable existe
        Optional<RegisterUser> responsible = registerUserRepository.findById(responsibleId);
        if (responsible.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Expense expense = expenseOpt.get();
        expense.setTitle(title);
        expense.setDescription(description);
        expense.setPeriod(period);
        expense.setResponsible(responsible.get());
        expense.setValue(value);
        expense.setCategory(category);

        // Validar período
        if (!expense.isValidPeriod()) {
            throw new IllegalArgumentException("Período inválido: " + period);
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
    @Transactional(readOnly = true)
    public boolean userHasExpenses(UUID userId) {
        Optional<RegisterUser> user = registerUserRepository.findById(userId);
        if (user.isPresent()) {
            List<Expense> expenses = expenseRepository.findByResponsible(user.get());
            return !expenses.isEmpty();
        }
        return false;
    }

    /**
     * Obtener gastos por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDateRange(LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Obtener gastos de un usuario en un rango de fechas
     */
    @Transactional(readOnly = true)
    public List<Expense> getUserExpensesByDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        Optional<RegisterUser> user = registerUserRepository.findById(userId);
        if (user.isPresent()) {
            return expenseRepository.findByResponsibleAndDateRange(user.get(), start, end);
        }
        return List.of();
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
     * Validar si se puede eliminar un usuario
     */
    @Transactional(readOnly = true)
    public boolean canDeleteUser(UUID userId) {
        return !userHasExpenses(userId);
    }

    /**
     * Transferir gastos de un usuario a otro
     */
    public int transferExpenses(UUID fromUserId, UUID toUserId) {
        Optional<RegisterUser> fromUser = registerUserRepository.findById(fromUserId);
        Optional<RegisterUser> toUser = registerUserRepository.findById(toUserId);

        if (fromUser.isEmpty() || toUser.isEmpty()) {
            throw new IllegalArgumentException("Uno o ambos usuarios no existen");
        }

        List<Expense> expenses = expenseRepository.findByResponsible(fromUser.get());

        expenses.forEach(expense -> expense.setResponsible(toUser.get()));

        expenseRepository.saveAll(expenses);
        return expenses.size();
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
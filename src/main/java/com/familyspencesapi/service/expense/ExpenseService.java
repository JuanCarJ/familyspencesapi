package com.familyspencesapi.service.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.family.FamilyMember;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.family.FamilyMemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    /**
     * Calcular total de gastos por período
     */
    public BigDecimal calculateTotalByPeriod(String period) {
        return expenseRepository.calculateTotalByPeriod(period);
    }

    /**
     * Calcular total de gastos por categoría
     */
    public BigDecimal calculateTotalByCategory(ExpenseCategory category) {
        return expenseRepository.calculateTotalByCategory(category);
    }

    /**
     * Calcular total de gastos por responsable
     */
    public BigDecimal calculateTotalByResponsible(UUID responsibleId) {
        Optional<FamilyMember> responsible = familyMemberRepository.findById(responsibleId);
        if (responsible.isPresent()) {
            return expenseRepository.calculateTotalByResponsible(responsible.get());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Obtener estadísticas generales de gastos
     */
    public Object getExpenseStatistics() {
        Object[] basicStats = expenseRepository.getBasicStatistics();

        long totalExpenses = ((Number) basicStats[0]).longValue();
        BigDecimal totalAmount = (BigDecimal) basicStats[1];
        BigDecimal averageAmount = (BigDecimal) basicStats[2];

        // Encontrar la categoría más cara
        List<Object[]> categoryTotals = expenseRepository.findCategoryTotals();
        String mostExpensiveCategoryName = "N/A";

        if (!categoryTotals.isEmpty()) {
            ExpenseCategory topCategory = (ExpenseCategory) categoryTotals.get(0)[0];
            mostExpensiveCategoryName = topCategory.getDisplayName();
        }

        // Crear una variable final para usar en el objeto anónimo
        final String finalMostExpensiveCategory = mostExpensiveCategoryName;

        // Crear un objeto simple con estadísticas básicas
        return new Object() {
            public final long totalExpenses = ((Number) basicStats[0]).longValue();
            public final BigDecimal totalAmount = basicStats[1] != null ? (BigDecimal) basicStats[1] : BigDecimal.ZERO;
            public final BigDecimal averageAmount = basicStats[2] != null ? ((BigDecimal) basicStats[2]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            public final String mostExpensiveCategory = finalMostExpensiveCategory;
        };
    }

    /**
     * Buscar gastos con múltiples filtros
     */
    public List<Expense> searchExpenses(String title, String period, ExpenseCategory category,
                                        UUID responsibleId, Pageable pageable) {
        FamilyMember responsible = null;
        if (responsibleId != null) {
            Optional<FamilyMember> responsibleOpt = familyMemberRepository.findById(responsibleId);
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
        Optional<FamilyMember> responsible = familyMemberRepository.findById(responsibleId);
        if (responsible.isEmpty()) {
            throw new IllegalArgumentException("Miembro de familia no encontrado");
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
            // Solo advertir, no bloquear (puedes cambiar esta lógica según tus necesidades)
            System.out.println("Advertencia: Ya existe un gasto similar");
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
        Optional<FamilyMember> responsible = familyMemberRepository.findById(responsibleId);
        if (responsible.isEmpty()) {
            throw new IllegalArgumentException("Miembro de familia no encontrado");
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
    public List<Expense> getRecentExpenses(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return expenseRepository.findRecentExpenses(since);
    }

    /**
     * Obtener gastos del mes actual
     */
    public List<Expense> getCurrentMonthExpenses() {
        return expenseRepository.findCurrentMonthExpenses();
    }

    /**
     * Obtener top gastos por valor
     */
    public List<Expense> getTopExpensesByValue(int limit) {
        return expenseRepository.findTopExpensesByValue(
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }

    /**
     * Verificar si un miembro tiene gastos asociados
     */
    public boolean memberHasExpenses(UUID memberId) {
        Optional<FamilyMember> member = familyMemberRepository.findById(memberId);
        if (member.isPresent()) {
            List<Expense> expenses = expenseRepository.findByResponsible(member.get());
            return !expenses.isEmpty();
        }
        return false;
    }

    /**
     * Obtener gastos por rango de fechas
     */
    public List<Expense> getExpensesByDateRange(LocalDateTime start, LocalDateTime end) {
        return expenseRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Obtener gastos de un miembro en un rango de fechas
     */
    public List<Expense> getMemberExpensesByDateRange(UUID memberId, LocalDateTime start, LocalDateTime end) {
        Optional<FamilyMember> member = familyMemberRepository.findById(memberId);
        if (member.isPresent()) {
            return expenseRepository.findByResponsibleAndDateRange(member.get(), start, end);
        }
        return List.of();
    }

    /**
     * Calcular promedio de gastos por categoría
     */
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
    public List<Object[]> getExpenseCountByCategory() {
        return expenseRepository.countExpensesByCategory();
    }

    /**
     * Validar si se puede eliminar un miembro de familia
     */
    public boolean canDeleteFamilyMember(UUID memberId) {
        return !memberHasExpenses(memberId);
    }

    /**
     * Transferir gastos de un miembro a otro
     */
    public int transferExpenses(UUID fromMemberId, UUID toMemberId) {
        Optional<FamilyMember> fromMember = familyMemberRepository.findById(fromMemberId);
        Optional<FamilyMember> toMember = familyMemberRepository.findById(toMemberId);

        if (fromMember.isEmpty() || toMember.isEmpty()) {
            throw new IllegalArgumentException("Uno o ambos miembros no existen");
        }

        List<Expense> expenses = expenseRepository.findByResponsible(fromMember.get());

        expenses.forEach(expense -> {
            expense.setResponsible(toMember.get());
        });

        expenseRepository.saveAll(expenses);
        return expenses.size();
    }
}
package com.familyspencesapi.repositories.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.users.RegisterUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByCategory(ExpenseCategory category);

    List<Expense> findByPeriodIgnoreCase(String period);

    List<Expense> findByResponsible(RegisterUser responsible);

    // Buscar por ID del responsable
    List<Expense> findByResponsibleId(UUID responsibleId);

    // Buscar por familia del responsable
    @Query("SELECT e FROM Expense e WHERE e.responsible.family.id = :familyId")
    List<Expense> findByResponsibleFamilyId(@Param("familyId") UUID familyId);

    // Buscar por fecha de creación
    List<Expense> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);


    // Consultas personalizadas con @Query
    // Calcular total por período
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE LOWER(e.period) = LOWER(:period)")
    BigDecimal calculateTotalByPeriod(@Param("period") String period);

    // Calcular total por categoría
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.category = :category")
    BigDecimal calculateTotalByCategory(@Param("category") ExpenseCategory category);

    // Calcular total por responsable
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.responsible = :responsible")
    BigDecimal calculateTotalByResponsible(@Param("responsible") RegisterUser responsible);

    // Calcular total por familia
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.responsible.family.id = :familyId")
    BigDecimal calculateTotalByFamily(@Param("familyId") UUID familyId);

    // Calcular total por familia y período
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.responsible.family.id = :familyId AND LOWER(e.period) = LOWER(:period)")
    BigDecimal calculateTotalByFamilyAndPeriod(@Param("familyId") UUID familyId, @Param("period") String period);

    // Obtener estadísticas básicas
    @Query("SELECT COUNT(e), COALESCE(SUM(e.value), 0), COALESCE(AVG(e.value), 0) FROM Expense e")
    Object[] getBasicStatistics();

    // Encontrar la categoría más cara
    @Query("SELECT e.category, SUM(e.value) as total FROM Expense e GROUP BY e.category ORDER BY total DESC")
    List<Object[]> findCategoryTotals();

    // Buscar gastos por múltiples criterios
    @Query("SELECT e FROM Expense e WHERE " +
            "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:period IS NULL OR LOWER(e.period) = LOWER(:period)) AND " +
            "(:category IS NULL OR e.category = :category) AND " +
            "(:responsible IS NULL OR e.responsible = :responsible)")
    Page<Expense> findExpensesByCriteria(
            @Param("title") String title,
            @Param("period") String period,
            @Param("category") ExpenseCategory category,
            @Param("responsible") RegisterUser responsible,
            Pageable pageable);

    // Encontrar gastos del mes actual
    @Query("SELECT e FROM Expense e WHERE MONTH(e.createdAt) = MONTH(CURRENT_DATE) AND YEAR(e.createdAt) = YEAR(CURRENT_DATE)")
    List<Expense> findCurrentMonthExpenses();

    // Encontrar top gastos por valor
    @Query("SELECT e FROM Expense e ORDER BY e.value DESC")
    Page<Expense> findTopExpensesByValue(Pageable pageable);

    // Contar gastos por categoría
    @Query("SELECT e.category, COUNT(e) FROM Expense e GROUP BY e.category")
    List<Object[]> countExpensesByCategory();

    // Buscar gastos por rango de fechas y responsable
    @Query("SELECT e FROM Expense e WHERE e.responsible = :responsible AND e.createdAt BETWEEN :startDate AND :endDate")
    List<Expense> findByResponsibleAndDateRange(
            @Param("responsible") RegisterUser responsible,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Verificar si existe un gasto similar (mismo título, período y responsable)
    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE LOWER(e.title) = LOWER(:title) AND LOWER(e.period) = LOWER(:period) AND e.responsible = :responsible AND e.id != :excludeId")
    boolean existsSimilarExpense(
            @Param("title") String title,
            @Param("period") String period,
            @Param("responsible") RegisterUser responsible,
            @Param("excludeId") UUID excludeId);

    // Buscar gastos recientes (últimos N días)
    @Query("SELECT e FROM Expense e WHERE e.createdAt >= :since ORDER BY e.createdAt DESC")
    List<Expense> findRecentExpenses(@Param("since") LocalDateTime since);

    // Encontrar gastos caros (usando método del dominio)
    @Query("SELECT e FROM Expense e WHERE e.value > 1000.00")
    List<Expense> findExpensiveExpenses();

    // Estadísticas por familia
    @Query("SELECT e.responsible.family.id, COUNT(e), SUM(e.value) FROM Expense e GROUP BY e.responsible.family.id")
    List<Object[]> getExpenseStatisticsByFamily();

    // Gastos por usuario en un período específico
    @Query("SELECT e FROM Expense e WHERE e.responsible.id = :userId AND LOWER(e.period) = LOWER(:period)")
    List<Expense> findByUserIdAndPeriod(@Param("userId") UUID userId, @Param("period") String period);
}
package com.familyspencesapi.repositories.expense;

import com.familyspencesapi.domain.expense.Expense;

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

    List<Expense> findByPeriodIgnoreCase(String period);

    List<Expense> findByResponsible(String responsible);

    List<Expense> findByResponsibleAndPeriod(String responsible, String period);

    // Buscar todos los gastos por familia
    @Query("SELECT e FROM Expense e WHERE e.familyId = :familyId")
    List<Expense> findByResponsibleFamilyId(@Param("familyId") UUID familyId);

    // Buscar por fecha de creación
    List<Expense> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);


    // Consultas personalizadas con @Query
    // Calcular total por período
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE LOWER(e.period) = LOWER(:period)")
    BigDecimal calculateTotalByPeriod(@Param("period") String period);

    // Calcular total por responsable
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.responsible = :responsible")
    BigDecimal calculateTotalByResponsible(@Param("responsible") String responsible);

    // Calcular total por familia
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.familyId = :familyId")
    BigDecimal calculateTotalByFamily(@Param("familyId") UUID familyId);

    // Calcular total por familia y período
    @Query("""
    SELECT COALESCE(SUM(e.value), 0)
    FROM Expense e
    WHERE e.familyId = :familyId
      AND LOWER(e.period) = LOWER(:period)
""")
    BigDecimal calculateTotalByFamilyAndPeriod(@Param("familyId") UUID familyId, @Param("period") String period);

    // Obtener estadísticas básicas
    @Query("SELECT COUNT(e), COALESCE(SUM(e.value), 0), COALESCE(AVG(e.value), 0) FROM Expense e")
    Object[] getBasicStatistics();

    // Encontrar la categoría más cara
    @Query("SELECT e.category, SUM(e.value) as total FROM Expense e GROUP BY e.category ORDER BY total DESC")
    List<Object[]> findCategoryTotals();


    // Encontrar gastos del mes actual
    @Query("SELECT e FROM Expense e WHERE MONTH(e.createdAt) = MONTH(CURRENT_DATE) AND YEAR(e.createdAt) = YEAR(CURRENT_DATE)")
    List<Expense> findCurrentMonthExpenses();

    // Encontrar top gastos por valor
    @Query("SELECT e FROM Expense e ORDER BY e.value DESC")
    Page<Expense> findTopExpensesByValue(Pageable pageable);

    // Contar gastos por categoría
    @Query("SELECT e.category, COUNT(e) FROM Expense e GROUP BY e.category")
    List<Object[]> countExpensesByCategory();

    // Verificar si existe un gasto similar (mismo título, período y responsable)
    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE LOWER(e.title) = LOWER(:title) AND LOWER(e.period) = LOWER(:period) AND e.responsible = :responsible AND e.id != :excludeId")
    boolean existsSimilarExpense(
            @Param("title") String title,
            @Param("period") String period,
            @Param("responsible") String responsible,
            @Param("excludeId") UUID excludeId);

    // Buscar gastos recientes (últimos N días)
    @Query("SELECT e FROM Expense e WHERE e.createdAt >= :since ORDER BY e.createdAt DESC")
    List<Expense> findRecentExpenses(@Param("since") LocalDateTime since);

    // Encontrar gastos caros (usando método del dominio)
    @Query("SELECT e FROM Expense e WHERE e.value > 1000.00")
    List<Expense> findExpensiveExpenses();

}
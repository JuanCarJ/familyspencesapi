package com.familyspencesapi.repositories.expense;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.family.FamilyMember;

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

    // Métodos originales de búsqueda
    Page<Expense> findByCategory(ExpenseCategory category, Pageable pageable);
    List<Expense> findByCategory(ExpenseCategory category);

    Page<Expense> findByPeriodIgnoreCase(String period, Pageable pageable);
    List<Expense> findByPeriodIgnoreCase(String period);

    Page<Expense> findByResponsible(FamilyMember responsible, Pageable pageable);
    List<Expense> findByResponsible(FamilyMember responsible);

    List<Expense> findByValueGreaterThan(BigDecimal value);
    List<Expense> findByValueBetween(BigDecimal minValue, BigDecimal maxValue);

    Page<Expense> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    List<Expense> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Consultas personalizadas originales
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE LOWER(e.period) = LOWER(:period)")
    BigDecimal calculateTotalByPeriod(@Param("period") String period);

    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.category = :category")
    BigDecimal calculateTotalByCategory(@Param("category") ExpenseCategory category);

    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.responsible = :responsible")
    BigDecimal calculateTotalByResponsible(@Param("responsible") FamilyMember responsible);

    @Query("SELECT COUNT(e), COALESCE(SUM(e.value), 0), COALESCE(AVG(e.value), 0) FROM Expense e")
    Object[] getBasicStatistics();

    @Query("SELECT e.category, SUM(e.value) as total FROM Expense e GROUP BY e.category ORDER BY total DESC")
    List<Object[]> findCategoryTotals();

    @Query("SELECT e FROM Expense e WHERE " +
            "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:period IS NULL OR LOWER(e.period) = LOWER(:period)) AND " +
            "(:category IS NULL OR e.category = :category) AND " +
            "(:responsible IS NULL OR e.responsible = :responsible)")
    Page<Expense> findExpensesByCriteria(
            @Param("title") String title,
            @Param("period") String period,
            @Param("category") ExpenseCategory category,
            @Param("responsible") FamilyMember responsible,
            Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE MONTH(e.createdAt) = MONTH(CURRENT_DATE) AND YEAR(e.createdAt) = YEAR(CURRENT_DATE)")
    List<Expense> findCurrentMonthExpenses();

    @Query("SELECT e FROM Expense e ORDER BY e.value DESC")
    Page<Expense> findTopExpensesByValue(Pageable pageable);

    @Query("SELECT e.category, COUNT(e) FROM Expense e GROUP BY e.category")
    List<Object[]> countExpensesByCategory();

    @Query("SELECT e FROM Expense e WHERE e.responsible = :responsible AND e.createdAt BETWEEN :startDate AND :endDate")
    List<Expense> findByResponsibleAndDateRange(
            @Param("responsible") FamilyMember responsible,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE LOWER(e.title) = LOWER(:title) AND LOWER(e.period) = LOWER(:period) AND e.responsible = :responsible AND e.id != :excludeId")
    boolean existsSimilarExpense(
            @Param("title") String title,
            @Param("period") String period,
            @Param("responsible") FamilyMember responsible,
            @Param("excludeId") UUID excludeId);

    @Query("SELECT e FROM Expense e WHERE e.createdAt >= :since ORDER BY e.createdAt DESC")
    List<Expense> findRecentExpenses(@Param("since") LocalDateTime since);

    // ✅ Nuevo método agregado para el balance
    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e")
    BigDecimal sumAllExpenses();
}

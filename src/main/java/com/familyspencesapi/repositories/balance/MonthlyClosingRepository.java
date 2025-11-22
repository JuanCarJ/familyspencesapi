package com.familyspencesapi.repositories.balance;

import com.familyspencesapi.domain.home.Closings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonthlyClosingRepository extends JpaRepository<Closings, UUID> {
    List<Closings> findByFamilyIdOrderByClosingDateDesc(UUID familyId);

    boolean existsByFamilyIdAndClosingDateBetween(UUID familyId, java.time.LocalDate startDate, java.time.LocalDate endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.familyId = :familyId AND e.createdAt BETWEEN :startDate AND :endDate")
    java.math.BigDecimal calculateMonthlyExpenses(@org.springframework.data.repository.query.Param("familyId") UUID familyId, @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate, @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(i.total), 0) FROM Income i WHERE i.family = :familyId AND i.period = :period")
    Double calculateMonthlyIncome(@org.springframework.data.repository.query.Param("familyId") UUID familyId, @org.springframework.data.repository.query.Param("period") String period);
}

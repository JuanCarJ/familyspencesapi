package com.familyspencesapi.repositories.income;

import com.familyspencesapi.domain.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    // Suma total de todos los ingresos
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Income i")
    BigDecimal sumAllIncomes();
}

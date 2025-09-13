package com.familyspencesapi.repository.home;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class HomeRepository {

    private final JdbcTemplate jdbcTemplate;

    public HomeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public BigDecimal getTotalIncomes(String userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM incomes WHERE user_id = ?::uuid",
                BigDecimal.class,
                userId
        );
    }

    public BigDecimal getTotalExpenses(String userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(value), 0) FROM expenses WHERE user_id = ?::uuid",
                BigDecimal.class,
                userId
        );
    }

    public BigDecimal getLastIncome(String userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(amount), 0) FROM incomes WHERE user_id = ?::uuid",
                BigDecimal.class,
                userId
        );
    }
}

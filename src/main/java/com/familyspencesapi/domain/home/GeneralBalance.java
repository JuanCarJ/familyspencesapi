package com.familyspencesapi.domain.home;


import java.math.BigDecimal;

public record GeneralBalance(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance
) {}

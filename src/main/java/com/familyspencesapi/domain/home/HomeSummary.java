package com.familyspencesapi.domain.home;

import java.math.BigDecimal;

public class HomeSummary {
    private BigDecimal totalIncomes;
    private BigDecimal totalExpenses;
    private BigDecimal generalBalance;
    private BigDecimal lastIncome;

    public HomeSummary(BigDecimal totalIncomes, BigDecimal totalExpenses, BigDecimal generalBalance, BigDecimal lastIncome) {
        this.totalIncomes = totalIncomes;
        this.totalExpenses = totalExpenses;
        this.generalBalance = generalBalance;
        this.lastIncome = lastIncome;
    }

    public BigDecimal getTotalIncomes() {
        return totalIncomes;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getGeneralBalance() {
        return generalBalance;
    }

    public BigDecimal getLastIncome() {
        return lastIncome;
    }
}

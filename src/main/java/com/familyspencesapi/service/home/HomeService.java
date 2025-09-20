package com.familyspencesapi.services.home;

import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.income.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class HomeService {

    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public HomeService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    // Total de ingresos con valor seguro
    public BigDecimal getTotalIncomes() {
        BigDecimal total = incomeRepository.sumAllIncomes();
        return total != null ? total : BigDecimal.ZERO;
    }

    // Total de gastos con valor seguro
    public BigDecimal getTotalExpenses() {
        BigDecimal total = expenseRepository.sumAllExpenses();
        return total != null ? total : BigDecimal.ZERO;
    }

    // Balance = Ingresos - Gastos
    public BigDecimal getBalance() {
        return getTotalIncomes().subtract(getTotalExpenses());
    }
}

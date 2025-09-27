package com.familyspencesapi.service.home;

import com.familyspencesapi.domain.home.GeneralBalance;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.service.income.IncomeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class HomeService {

    private final ExpenseRepository expenseRepository;
    private final IncomeService incomeService;

    public HomeService(ExpenseRepository expenseRepository, IncomeService incomeService) {
        this.expenseRepository = expenseRepository;
        this.incomeService = incomeService;
    }

    @Transactional(readOnly = true)
    public GeneralBalance calculateGeneralBalance(UUID familyId) {
        BigDecimal totalExpenses = expenseRepository.calculateTotalByFamily(familyId);

        Double totalIncomeDouble = incomeService.getTotalByFamilyId(familyId);
        BigDecimal totalIncome = BigDecimal.valueOf(totalIncomeDouble != null ? totalIncomeDouble : 0.0);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return new GeneralBalance(totalIncome, totalExpenses, balance);
    }
}
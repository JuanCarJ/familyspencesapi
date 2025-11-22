package com.familyspencesapi.service.balance;

import com.familyspencesapi.config.messages.budgetprocessor.expense.ClosingProducerQueueConfig;
import com.familyspencesapi.domain.home.GeneralBalance;
import com.familyspencesapi.domain.home.MonthlyClosing;
import com.familyspencesapi.domain.home.Closings;
import com.familyspencesapi.messages.balance.BalanceMessageSenderBroker;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.balance.MonthlyClosingRepository;
import com.familyspencesapi.service.income.IncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class BalanceService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);

    private final ExpenseRepository expenseRepository;
    private final IncomeService incomeService;
    private final BalanceMessageSenderBroker messageSender;
    private final ClosingProducerQueueConfig config;
    private final MonthlyClosingRepository closingRepository;

    public BalanceService(ExpenseRepository expenseRepository, IncomeService incomeService,
                          BalanceMessageSenderBroker messageSender,
                          ClosingProducerQueueConfig config,
                          MonthlyClosingRepository closingRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeService = incomeService;
        this.messageSender = messageSender;
        this.config = config;
        this.closingRepository = closingRepository;
    }

    @Transactional(readOnly = true)
    public GeneralBalance calculateGeneralBalance(UUID familyId) {
        BigDecimal totalExpenses = expenseRepository.calculateTotalByFamily(familyId);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        Double totalIncomeDouble = incomeService.getTotalByFamilyId(familyId);
        BigDecimal totalIncome = BigDecimal.valueOf(totalIncomeDouble != null ? totalIncomeDouble : 0.0);

        BigDecimal balance = totalIncome.subtract(totalExpenses);

        return new GeneralBalance(totalIncome, totalExpenses, balance);
    }

    public void initiateMonthlyClosing(UUID familyId, java.time.YearMonth targetMonth) {
        logger.info("Initiating monthly closing process for familyId: {} and month: {}", familyId, targetMonth);

        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();

        boolean exists = closingRepository.existsByFamilyIdAndClosingDateBetween(familyId, startOfMonth, endOfMonth);
        if (exists) {
            throw new IllegalStateException("A monthly closing already exists for month " + targetMonth);
        }

        BigDecimal totalExpenses = closingRepository.calculateMonthlyExpenses(
                familyId,
                startOfMonth.atStartOfDay(),
                endOfMonth.atTime(23, 59, 59)
        );
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        String period = targetMonth.toString();
        Double totalIncomeDouble = closingRepository.calculateMonthlyIncome(familyId, period);
        BigDecimal totalIncome = BigDecimal.valueOf(totalIncomeDouble != null ? totalIncomeDouble : 0.0);

        BigDecimal balance = totalIncome.subtract(totalExpenses);
        GeneralBalance currentBalance = new GeneralBalance(totalIncome, totalExpenses, balance);


        MonthlyClosing closingData = new MonthlyClosing(
                familyId,
                currentBalance,
                endOfMonth
        );

        messageSender.send(
                closingData,
                config.getExchangeName(),
                config.getRoutingKey()
        );

        logger.info("Monthly closing message sent for familyId: {}", familyId);
    }

    @Transactional(readOnly = true)
    public List<Closings> getClosingHistoryForFamily(UUID familyId) {
        return closingRepository.findByFamilyIdOrderByClosingDateDesc(familyId);
    }
}
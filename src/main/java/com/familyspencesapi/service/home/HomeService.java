package com.familyspencesapi.service.home;

import com.familyspencesapi.config.messages.budgetprocessor.expense.ClosingProducerQueueConfig;
import com.familyspencesapi.domain.home.GeneralBalance;
import com.familyspencesapi.domain.home.MonthlyClosing;
import com.familyspencesapi.domain.home.Closings;
import com.familyspencesapi.messages.users.MessageSenderBroker;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.balance.MonthlyClosingRepository;
import com.familyspencesapi.service.income.IncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class HomeService {

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

    private final ExpenseRepository expenseRepository;
    private final IncomeService incomeService;
    private final MessageSenderBroker messageSender;
    private final ClosingProducerQueueConfig config;
    private final MonthlyClosingRepository closingRepository;

    public HomeService(ExpenseRepository expenseRepository, IncomeService incomeService,
                       MessageSenderBroker messageSender, ClosingProducerQueueConfig config,
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

    public void initiateMonthlyClosing(UUID familyId) {
        logger.info("Initiating monthly closing process for familyId: {}", familyId);

        GeneralBalance currentBalance = calculateGeneralBalance(familyId);

        MonthlyClosing closingData = new MonthlyClosing(
                familyId,
                currentBalance,
                LocalDate.now()
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
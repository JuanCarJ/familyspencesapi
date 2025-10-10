package com.familyspencesapi.service.home;

import com.familyspencesapi.config.messages.users.ClosingProducerQueueConfig;
import com.familyspencesapi.domain.home.GeneralBalance;
import com.familyspencesapi.domain.home.MonthlyClosing;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.service.income.IncomeService;
import com.familyspencesapi.messages.users.MessageSenderBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class HomeService {

    private static final Logger logger = LoggerFactory.getLogger(HomeService.class);

    private final ExpenseRepository expenseRepository;
    private final IncomeService incomeService;
    private final MessageSenderBroker messageSender;
    private final ClosingProducerQueueConfig config;

    public HomeService(ExpenseRepository expenseRepository, IncomeService incomeService,
                       MessageSenderBroker messageSender, ClosingProducerQueueConfig config) {
        this.expenseRepository = expenseRepository;
        this.incomeService = incomeService;
        this.messageSender = messageSender;
        this.config = config;
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
}
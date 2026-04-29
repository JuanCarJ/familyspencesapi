package com.familyspencesapi.service.balance;

import com.familyspencesapi.config.messages.budgetprocessor.expense.ClosingProducerQueueConfig;
import com.familyspencesapi.domain.home.MonthlyClosing;
import com.familyspencesapi.messages.balance.BalanceMessageSenderBroker;
import com.familyspencesapi.repositories.balance.MonthlyClosingRepository;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.service.income.IncomeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private IncomeService incomeService;
    @Mock
    private BalanceMessageSenderBroker messageSender;
    @Mock
    private ClosingProducerQueueConfig config;
    @Mock
    private MonthlyClosingRepository closingRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void monthlyClosingUsesPeriodForIncomeAndExpenses() {
        UUID familyId = UUID.randomUUID();
        YearMonth targetMonth = YearMonth.of(2026, 4);

        when(closingRepository.countByFamilyIdAndClosingDateBetween(
                familyId,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        )).thenReturn(0L);
        when(closingRepository.calculateMonthlyExpenses(familyId, "2026-04"))
                .thenReturn(new BigDecimal("125000.50"));
        when(closingRepository.calculateMonthlyIncome(familyId, "2026-04"))
                .thenReturn(300000.75);
        when(config.getExchangeName()).thenReturn("x.closing.events");
        when(config.getRoutingKey()).thenReturn("event.month.close.request");

        balanceService.initiateMonthlyClosing(familyId, targetMonth);

        ArgumentCaptor<MonthlyClosing> closingCaptor = ArgumentCaptor.forClass(MonthlyClosing.class);
        verify(messageSender).send(closingCaptor.capture(), org.mockito.Mockito.eq("x.closing.events"), org.mockito.Mockito.eq("event.month.close.request"));

        MonthlyClosing closing = closingCaptor.getValue();
        assertEquals(familyId, closing.familyId());
        assertEquals(LocalDate.of(2026, 4, 30), closing.closingDate());
        assertEquals(BigDecimal.valueOf(300000.75), closing.balance().totalIncome());
        assertEquals(new BigDecimal("125000.50"), closing.balance().totalExpenses());
        assertEquals(BigDecimal.valueOf(175000.25), closing.balance().balance());
    }

    @Test
    void monthlyClosingRejectsExistingClosingForMonth() {
        UUID familyId = UUID.randomUUID();
        YearMonth targetMonth = YearMonth.of(2026, 4);

        when(closingRepository.countByFamilyIdAndClosingDateBetween(
                familyId,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        )).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> balanceService.initiateMonthlyClosing(familyId, targetMonth));
        verify(messageSender, never()).send(org.mockito.Mockito.any(), org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString());
    }

    @Test
    void monthlyClosingRejectsDuplicateExistingClosingsForMonth() {
        UUID familyId = UUID.randomUUID();
        YearMonth targetMonth = YearMonth.of(2026, 4);

        when(closingRepository.countByFamilyIdAndClosingDateBetween(
                familyId,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 30)
        )).thenReturn(2L);

        assertThrows(IllegalStateException.class, () -> balanceService.initiateMonthlyClosing(familyId, targetMonth));
        verify(messageSender, never()).send(org.mockito.Mockito.any(), org.mockito.Mockito.anyString(), org.mockito.Mockito.anyString());
    }
}

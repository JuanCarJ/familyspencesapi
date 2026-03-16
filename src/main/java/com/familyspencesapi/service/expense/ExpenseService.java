package com.familyspencesapi.service.expense;

import com.familyspencesapi.config.messages.budgetprocessor.expense.BudgetExpenseProcessQueueConfig;
import com.familyspencesapi.controllers.expense.ExpenseRequest;
import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.messages.expense.MessageSenderBrokerExpense;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ExpenseService {


    private final ExpenseRepository expenseRepository;
    private final RegisterUserRepository registerUserRepository;
    private final MessageSenderBrokerExpense messageSenderBrokerExpense;
    private final BudgetExpenseProcessQueueConfig processQueueConfig;


    public ExpenseService(ExpenseRepository expenseRepository, RegisterUserRepository registerUserRepository, MessageSenderBrokerExpense messageSenderBrokerExpense, BudgetExpenseProcessQueueConfig processQueueConfig) {
        this.expenseRepository = expenseRepository;
        this.registerUserRepository = registerUserRepository;
        this.messageSenderBrokerExpense = messageSenderBrokerExpense;
        this.processQueueConfig = processQueueConfig;
    }

    /**
     * Encontrar todos los gastos
     */
    @Transactional(readOnly = true)
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    /**
     * Encontrar gasto por ID
     */
    @Transactional(readOnly = true)
    public Expense findById(UUID id) {
        return expenseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Expense not found"));
    }

    /**
     * Guardar gasto
     */
    public String save(ExpenseRequest request, String userMail, UUID familyId) {
        Optional<RegisterUser> userOpt = registerUserRepository.findByEmail(userMail);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Responsible not found");
        }

        Expense expense = new Expense(
                request.getTitle().trim(),
                request.getDescription() != null ? request.getDescription().trim() : "",
                request.getPeriod().trim(),
                request.getResponsible(),
                request.getValue(),
                request.getCategory(),
                familyId
        );

        if (!expense.isValidPeriod()) {
            throw new IllegalArgumentException("Invalid period");
        }
        System.out.println("CATEGORY ENVIADA = " + request.getCategory());
        messageSenderBrokerExpense.execute(expense, processQueueConfig.getRoutingKeyExpenseCreate());

        return "The message was sent successfully.";
    }

    /**
     * Eliminar gasto por ID
     */
    public boolean deleteById(UUID id) {
        if (expenseRepository.existsById(id)) {
            Expense expense = new Expense(id);
            messageSenderBrokerExpense.execute(expense, processQueueConfig.getRoutingKeyExpenseDelete());
            return true;
        }
        return false;
    }

    /**
     * Encontrar gastos por período
     */
    @Transactional(readOnly = true)
    public List<Expense> findByPeriod(String period) {
        return expenseRepository.findByPeriodIgnoreCase(period);
    }

    /**
     * Encontrar gastos por responsable (usuario)
     */
    @Transactional(readOnly = true)
    public List<Expense> findByResponsibleId(String responsibleMail) {
        return expenseRepository.findByResponsible(responsibleMail);
    }

    /**
     * Encontrar gastos por familia
     */
    @Transactional(readOnly = true)
    public List<Expense> findByResponsibleFamilyId(UUID familyId) {
        return expenseRepository.findByResponsibleFamilyId(familyId);
    }

    /**
     * Calcular total de gastos por período
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByPeriod(String period) {
        return expenseRepository.calculateTotalByPeriod(period);
    }

    /**
     * Calcular total de gastos por familia y período
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalByFamilyAndPeriod(UUID familyId, String period) {
        return expenseRepository.calculateTotalByFamilyAndPeriod(familyId, period);
    }

    /**
     * Actualizar un gasto existente
     */
    public String updateExpense(ExpenseRequest request,String responsiblemail, UUID expenseId) {

        // Verificar que el gasto existe
        Optional<Expense> expenseOpt = expenseRepository.findById(expenseId);
        if (expenseOpt.isEmpty()) {
            throw new IllegalArgumentException("Gasto no encontrado");
        }

        // Verificar que el responsable existe
        Optional<RegisterUser> responsible = registerUserRepository.findByEmail(responsiblemail);
        if (responsible.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Expense expense = expenseOpt.get();
        expense.setTitle(request.getTitle().trim());
        expense.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        expense.setPeriod(request.getPeriod().trim());
        expense.setResponsible(request.getResponsible());
        expense.setValue(request.getValue());
        expense.setCategory(request.getCategory());

        if (!expense.isValidPeriod()) {
            throw new IllegalArgumentException("Período inválido: " + expense.getPeriod());
        }

        messageSenderBrokerExpense.execute(expense, processQueueConfig.getRoutingKeyExpenseUpdate());
        return "The message was sent successfully.";
    }

    /**
     * Obtener gastos recientes (últimos N días)
     */
    @Transactional(readOnly = true)
    public List<Expense> getRecentExpenses(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return expenseRepository.findRecentExpenses(since);
    }

    /**
     * Obtener gastos del mes actual
     */
    @Transactional(readOnly = true)
    public List<Expense> getCurrentMonthExpenses() {
        return expenseRepository.findCurrentMonthExpenses();
    }

    /**
     * Obtener top gastos por valor
     */
    @Transactional(readOnly = true)
    public List<Expense> getTopExpensesByValue(int limit) {
        return expenseRepository.findTopExpensesByValue(
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }


}
package com.familyspencesapi.service.budget;
import com.familyspencesapi.domain.budget.Budget;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.repositories.budget.IRepositoryBudget;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import com.familyspencesapi.service.income.IncomeService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private static final String BUDGET_ID = "budgetId";

    private final IRepositoryBudget repositoryBudget;
    private final ExpenseRepository expenseRepository;
    private final IncomeService incomeService;
    private final RegisterUserRepository registerUserRepository;

    public BudgetService(IRepositoryBudget repositoryBudget,
                         ExpenseRepository expenseRepository,
                         IncomeService incomeService,
                         RegisterUserRepository registerUserRepository1) {
        this.repositoryBudget = repositoryBudget;
        this.expenseRepository = expenseRepository;
        this.incomeService = incomeService;
        this.registerUserRepository = registerUserRepository1;
    }

    /** Busca un mienbro usando el responsibleId*/
    public Map<String, Object> createBudget(UUID familyId, CreateBudgetRequest request) {
        RegisterUser responsible = registerUserRepository.findById(request.responsibleId())
                .orElseThrow(() -> new IllegalArgumentException("El responsable con el ID proporcionado no existe."));

        Budget newBudget = new Budget();
        newBudget.setFamilyId(familyId);
        newBudget.setPeriod(request.period());
        newBudget.setBudgetAmount(request.budgetAmount());
        newBudget.setResponsible(responsible);

        Budget savedBudget = repositoryBudget.save(newBudget);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(BUDGET_ID, savedBudget.getBudgetId());
        response.put("familyId", savedBudget.getFamilyId());
        response.put("period", savedBudget.getPeriod().toString());
        response.put("budgetAmount", savedBudget.getBudgetAmount());
        response.put("responsibleId", savedBudget.getResponsible().getId());
        response.put("message", "Budget successfully added");
        return response;
    }
    /** Busca un presupuesto por ID*/
    public Map<String, Object> getBudgetDetails(UUID budgetId) {
        Budget budget = repositoryBudget.findById(budgetId)
                .orElseThrow(() -> new NoSuchElementException("Presupuesto no encontrado con ID: " + budgetId));

        /** LLama  para obtener el total de la familia*/
        Double totalIncome = incomeService.getTotalByFamilyId(budget.getFamilyId());

        /** Formatea la fecha*/
        String periodAsString = budget.getPeriod().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        BigDecimal totalExpenses = expenseRepository.calculateTotalByPeriod(periodAsString);
        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }

        BigDecimal balance = BigDecimal.valueOf(totalIncome).subtract(totalExpenses);

        Map<String, Object> responsibleMap = new LinkedHashMap<>();
        responsibleMap.put("responsibleId", budget.getResponsible().getId());
        responsibleMap.put("name", budget.getResponsible().getfullName());

        Map<String, Object> summaryMap = new LinkedHashMap<>();
        summaryMap.put("familyTotalIncome", totalIncome);
        summaryMap.put("totalExpenses", totalExpenses);
        summaryMap.put("balance", balance);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(BUDGET_ID, budget.getBudgetId());
        response.put("period", budget.getPeriod().toString());
        response.put("budgetAmount", budget.getBudgetAmount());
        response.put("responsible", responsibleMap);
        response.put("summary", summaryMap);

        return response;
    }
    /** Obtener todos los presupuestos por familia*/
    public List<Map<String, Object>> getAllBudgetsForFamily(UUID familyId) {
        List<Budget> budgets = repositoryBudget.findByFamilyIdOrderByPeriod(familyId);

        return budgets.stream().map(budget -> {
            BigDecimal budgetAmount = BigDecimal.valueOf(budget.getBudgetAmount());

            Double totalIncome = incomeService.getTotalByFamilyId(budget.getFamilyId());
            BigDecimal income = BigDecimal.valueOf(totalIncome);

            String periodAsString = budget.getPeriod().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal totalExpenses = expenseRepository.calculateTotalByPeriod(periodAsString);
            totalExpenses = (totalExpenses == null) ? BigDecimal.ZERO : totalExpenses;

            BigDecimal incomeDifference = income.subtract(budgetAmount);
            BigDecimal expensesDifference = budgetAmount.add(income).subtract(totalExpenses);
            BigDecimal balance = expensesDifference;

            Map<String, Object> budgetMap = new LinkedHashMap<>();
            budgetMap.put(BUDGET_ID, budget.getBudgetId());
            budgetMap.put("periodo", periodAsString);
            budgetMap.put("presupuesto", budget.getBudgetAmount());
            budgetMap.put("income", totalIncome);
            budgetMap.put("expenses", totalExpenses);
            budgetMap.put("incomeDifference", incomeDifference);
            budgetMap.put("expensesDifference", expensesDifference);
            budgetMap.put("balance", balance);
            budgetMap.put("responsable", budget.getResponsible().getfullName());

            return budgetMap;
        }).collect(Collectors.toList());
    }
}
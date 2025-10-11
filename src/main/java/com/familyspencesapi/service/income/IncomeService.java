package com.familyspencesapi.service.income;

import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.repositories.income.RepositoryIncome;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IncomeService {

    private final RepositoryIncome repositoryIncome;

    public IncomeService(RepositoryIncome repositoryIncome) {
        this.repositoryIncome = repositoryIncome;
    }

    public List<Income> getAllIncomes() {
        return repositoryIncome.findAll();
    }

    public List<Income> getIncomesByFamily(UUID familyId) {
        return repositoryIncome.findByFamily(familyId);
    }

    public List<Income> getIncomesByResponsible(UUID responsibleId) {
        return repositoryIncome.findByResponsible_Id(responsibleId);
    }

    public List<Income> getIncomesByPeriod(String period) {
        return repositoryIncome.findByPeriod(period);
    }

    public Income createIncome(Income income) {
        return repositoryIncome.save(income);
    }

    public void deleteIncome(UUID id) {
        repositoryIncome.deleteById(id);
    }
}

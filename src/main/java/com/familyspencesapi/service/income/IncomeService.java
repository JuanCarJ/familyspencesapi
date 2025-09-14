package com.familyspencesapi.service.income;

import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.repositories.income.RepositoryIncome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    @Transactional
    public Income createIncome(Income income) {
        if (income.getTotal() == null || income.getTotal() < 0) {
            throw new IllegalArgumentException("El total no puede ser nulo ni negativo");
        }
        return repositoryIncome.save(income);
    }

    public Optional<Income> getIncomeById(Long id) {
        return repositoryIncome.findById(id);
    }

    @Transactional
    public Optional<Income> updateIncome(Long id, Income updated) {
        return repositoryIncome.findById(id).map(income -> {
            income.setResponsible(updated.getResponsible());
            income.setTitle(updated.getTitle());
            income.setDescription(updated.getDescription());
            income.setPeriod(updated.getPeriod());
            income.setTotal(updated.getTotal());
            income.setFamilyId(updated.getFamilyId());
            return repositoryIncome.save(income);
        });
    }

    @Transactional
    public boolean deleteIncome(Long id) {
        if (!repositoryIncome.existsById(id)) return false;
        repositoryIncome.deleteById(id);
        return true;
    }

    public List<Income> getIncomesByFamilyId(UUID familyId) {
        return repositoryIncome.findByFamilyId(familyId);
    }

    public Double getTotalByFamilyId(UUID familyId) {
        return repositoryIncome.findByFamilyId(familyId)
                .stream()
                .mapToDouble(i -> i.getTotal() != null ? i.getTotal() : 0.0)
                .sum();
    }
}

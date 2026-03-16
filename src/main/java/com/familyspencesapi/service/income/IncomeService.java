package com.familyspencesapi.service.income;

import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.messages.income.MessageSenderBrokerIncome;
import com.familyspencesapi.repositories.income.RepositoryIncome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class IncomeService {

    private final RepositoryIncome repositoryIncome;
    private final MessageSenderBrokerIncome incomeSender;

    public IncomeService(RepositoryIncome repositoryIncome, MessageSenderBrokerIncome incomeSender) {
        this.repositoryIncome = repositoryIncome;
        this.incomeSender = incomeSender;
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
        Income createdIncome = repositoryIncome.save(income);
        incomeSender.sendIncomeCreated(createdIncome);
        return createdIncome;
    }

    @Transactional
    public void deleteIncome(UUID id) {
        repositoryIncome.deleteById(id);
        final Map<String, String> message = Map.of("incomeId", id.toString());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                incomeSender.sendIncomeDeleted(message);
            }
        });
    }

    public Double getTotalByFamilyId(UUID familyId) {
        Double total = repositoryIncome.sumTotalByFamilyId(familyId);
        return total != null ? total : 0.0;
    }

    @Transactional
    public Income updateIncome(UUID id, Income updatedIncome) {
        return repositoryIncome.findById(id)
                .map(existingIncome -> {
                    existingIncome.setTitle(updatedIncome.getTitle());
                    existingIncome.setDescription(updatedIncome.getDescription());
                    existingIncome.setPeriod(updatedIncome.getPeriod());
                    existingIncome.setTotal(updatedIncome.getTotal());
                    existingIncome.setResponsible(updatedIncome.getResponsible());
                    existingIncome.setFamily(updatedIncome.getFamily());
                    Income savedIncome = repositoryIncome.save(existingIncome);
                    incomeSender.sendIncomeUpdated(savedIncome);
                    return savedIncome;
                })
                .orElse(null); // No encontrado
    }
}
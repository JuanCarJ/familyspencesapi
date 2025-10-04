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

    //Obtener todos los ingresos
    public List<Income> getAllIncomes() {
        return repositoryIncome.findAll();
    }

    //Crear ingreso con validaciones
    @Transactional
    public Income createIncome(Income income) {
        if (income.getTotal() == null || income.getTotal() < 0) {
            throw new IllegalArgumentException("El total no puede ser nulo ni negativo");
        }
        if (income.getResponsible() == null || income.getResponsible().isBlank()) {
            throw new IllegalArgumentException("El responsable no puede estar vacío");
        }
        if (income.getTitle() == null || income.getTitle().isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        return repositoryIncome.save(income);
    }

    // Buscar por ID
    public Optional<Income> getIncomeById(Long id) {
        return repositoryIncome.findById(id);
    }

    // Actualizar ingreso
    @Transactional
    public Optional<Income> updateIncome(Long id, Income updated) {
        return repositoryIncome.findById(id).map(income -> {
            if (updated.getResponsible() != null) income.setResponsible(updated.getResponsible());
            if (updated.getTitle() != null) income.setTitle(updated.getTitle());
            if (updated.getDescription() != null) income.setDescription(updated.getDescription());
            if (updated.getPeriod() != null) income.setPeriod(updated.getPeriod());
            if (updated.getTotal() != null && updated.getTotal() >= 0) income.setTotal(updated.getTotal());
            if (updated.getFamilyId() != null) income.setFamilyId(updated.getFamilyId());

            return repositoryIncome.save(income);
        });
    }

    //Eliminar ingreso
    @Transactional
    public boolean deleteIncome(Long id) {
        if (!repositoryIncome.existsById(id)) return false;
        repositoryIncome.deleteById(id);
        return true;
    }

    // Buscar ingresos por familia
    public List<Income> getIncomesByFamilyId(UUID familyId) {
        return repositoryIncome.findByFamilyId(familyId);
    }

    public List<Income> getIncomesByResponsibleId(UUID responsibleId) {
        return repositoryIncome.findByResponsibleId(responsibleId);
    }

    //Sumar total de ingresos de una familia
    public Double getTotalByFamilyId(UUID familyId) {
        return repositoryIncome.findByFamilyId(familyId)
                .stream()
                .mapToDouble(i -> i.getTotal() != null ? i.getTotal() : 0.0)
                .sum();
    }
}

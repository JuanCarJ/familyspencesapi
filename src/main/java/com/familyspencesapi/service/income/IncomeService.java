
package com.familyspencesapi.service.income;

import com.familyspencesapi.domain.income.Income;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    private final List<Income> incomes = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1L);

    // Obtener todos los ingresos
    public List<Income> getAllIncomes() {
        return incomes;
    }

    // Crear un ingreso
    public Income createIncome(Income income) {
        income.setId(idCounter.getAndIncrement());
        incomes.add(income);
        return income;
    }

    // Buscar ingreso por ID
    public Optional<Income> getIncomeById(Long id) {
        return incomes.stream()
                .filter(i -> Objects.equals(i.getId(), id))
                .findFirst();
    }

    // Actualizar un ingreso
    public Optional<Income> updateIncome(Long id, Income updated) {
        return getIncomeById(id).map(income -> {
            income.setResponsible(updated.getResponsible());
            income.setTitle(updated.getTitle());
            income.setDescription(updated.getDescription());
            income.setPeriod(updated.getPeriod());
            income.setTotal(updated.getTotal());
            income.setFamilyId(updated.getFamilyId());
            return income;
        });
    }

    // Eliminar un ingreso
    public boolean deleteIncome(Long id) {
        return incomes.removeIf(i -> Objects.equals(i.getId(), id));
    }

    // Buscar ingresos por familia (UUID)
    public List<Income> getIncomesByFamilyId(UUID familyId) {
        return incomes.stream()
                .filter(i -> familyId.equals(i.getFamilyId()))
                .collect(Collectors.toList());
    }

    // Calcular total de ingresos por familia
    public Double getTotalByFamilyId(UUID familyId) {
        return incomes.stream()
                .filter(i -> familyId.equals(i.getFamilyId()))
                .mapToDouble(i -> i.getTotal() != null ? i.getTotal() : 0.0)
                .sum();
    }
}

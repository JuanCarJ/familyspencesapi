package com.familyspencesapi.domain.monthlyBudget;

import java.time.LocalDate;
import java.util.UUID;

public class Budget {

    private UUID budgetId;
    private LocalDate period;
    private double budgetAmount;
    private UUID familyId;
    private UUID responsibleId;

    public Budget(UUID budgetId, UUID responsibleId, double budgetAmount, UUID familyId, LocalDate period) {
        this.budgetId = budgetId;
        this.responsibleId = responsibleId;
        this.budgetAmount = budgetAmount;
        this.familyId = familyId;
        this.period = period;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(UUID responsibleId) {
        this.responsibleId = responsibleId;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public UUID getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(UUID budgetId) {
        this.budgetId = budgetId;
    }
}

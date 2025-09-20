package com.familyspencesapi.domain.monthlyBudget;

import com.familyspencesapi.domain.users.RegisterUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID budgetId;

    @NotNull(message = "El período es obligatorio")
    @Column(nullable = false)
    private LocalDate period;

    @NotNull(message = "El monto del presupuesto es obligatorio")
    @Positive(message = "El monto del presupuesto debe ser mayor que 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetAmount;

    @NotNull(message = "El ID de familia es obligatorio")
    @Column(nullable = false)
    private UUID familyId;

    @NotNull(message = "El responsable es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    private RegisterUser responsible;

    // Constructor vacío (requerido por JPA)
    public Budget() {
    }

    // Constructor completo
    public Budget(UUID budgetId, RegisterUser responsible, UUID familyId, BigDecimal budgetAmount, LocalDate period) {
        this.budgetId = budgetId;
        this.responsible = responsible;
        this.familyId = familyId;
        this.budgetAmount = budgetAmount;
        this.period = period;
    }

    // Constructor sin ID (para nuevos presupuestos)
    public Budget(RegisterUser responsible, UUID familyId, BigDecimal budgetAmount, LocalDate period) {
        this.responsible = responsible;
        this.familyId = familyId;
        this.budgetAmount = budgetAmount;
        this.period = period;
    }

    // Getters y Setters
    public UUID getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(UUID budgetId) {
        this.budgetId = budgetId;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public RegisterUser getResponsible() {
        return responsible;
    }

    public void setResponsible(RegisterUser responsible) {
        this.responsible = responsible;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    // Métodos de utilidad
    public String getResponsibleName() {
        return responsible != null ? responsible.getfullName() : "Sin responsable";
    }

    public UUID getResponsibleFamilyId() {
        return responsible != null ? responsible.getFamilyId() : null;
    }

    public boolean belongsToFamily(UUID familyId) {
        return familyId != null && familyId.equals(this.familyId);
    }

    public String getFormattedBudgetAmount() {
        return budgetAmount != null ? String.format("$%.2f", budgetAmount) : "$0.00";
    }

    public boolean isForPeriod(LocalDate period) {
        return this.period != null && this.period.equals(period);
    }

    // Método para verificar si el presupuesto es de la misma familia que el responsable
    public boolean isConsistent() {
        return familyId != null && familyId.equals(getResponsibleFamilyId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Budget)) return false;
        Budget budget = (Budget) o;
        return Objects.equals(budgetId, budget.budgetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetId);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "budgetId=" + budgetId +
                ", period=" + period +
                ", budgetAmount=" + getFormattedBudgetAmount() +
                ", familyId=" + familyId +
                ", responsible=" + getResponsibleName() +
                '}';
    }
}
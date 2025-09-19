//package com.familyspencesapi.domain.monthlyBudget;
//
//import com.familyspencesapi.domain.expense.Expense;
//import com.familyspencesapi.domain.family.FamilyMember;
//import com.familyspencesapi.domain.income.Income;
//import com.familyspencesapi.domain.users.RegisterUser;
//import jakarta.persistence.*;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//
//@Entity
//@Table(name = "budget")
//public class Budget {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID budgetId;
//
//    @Column(nullable = false)
//    private LocalDate period;
//
//    @Column(nullable = false)
//    private double budgetAmount;
//
//    @Column(nullable = false)
//    private UUID familyId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "responsible_id", nullable = false)
//    private FamilyMember responsible;
//
//
//    public Budget() {
//    }
//
//    public Budget(UUID budgetId, FamilyMember responsible, UUID familyId, double budgetAmount, LocalDate period) {
//        this.budgetId = budgetId;
//        this.responsible = responsible;
//        this.familyId = familyId;
//        this.budgetAmount = budgetAmount;
//        this.period = period;
//    }
//
//    public UUID getBudgetId() {
//        return budgetId;
//    }
//
//    public void setBudgetId(UUID budgetId) {
//        this.budgetId = budgetId;
//    }
//
//    public double getBudgetAmount() {
//        return budgetAmount;
//    }
//
//    public void setBudgetAmount(double budgetAmount) {
//        this.budgetAmount = budgetAmount;
//    }
//
//    public LocalDate getPeriod() {
//        return period;
//    }
//
//    public void setPeriod(LocalDate period) {
//        this.period = period;
//    }
//
//    public FamilyMember getResponsible() {
//        return responsible;
//    }
//
//    public void setResponsible(FamilyMember responsible) {
//        this.responsible = responsible;
//    }
//
//    public UUID getFamilyId() {
//        return familyId;
//    }
//
//    public void setFamilyId(UUID familyId) {
//        this.familyId = familyId;
//    }
//}
//
//

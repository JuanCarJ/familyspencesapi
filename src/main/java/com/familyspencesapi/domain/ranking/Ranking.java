package com.familyspencesapi.domain.ranking;

import com.familyspencesapi.domain.expenseControl.Expense;
import com.familyspencesapi.domain.income.Income;

import java.util.List;
import java.util.UUID;

public class Ranking {

    private UUID familyId;
    private List<String> nameFamilyMembers;
    private Expense expense;
    private Income income;

    public Ranking(final UUID familyId,final List<String> nameFamilyMembers,final Expense expense,final Income income) {
        this.familyId = familyId;
        this.nameFamilyMembers = nameFamilyMembers;
        this.expense = expense;
        this.income = income;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(final UUID familyId) {
        this.familyId = familyId;
    }

    public List<String> getNameFamilyMembers() {
        return nameFamilyMembers;
    }

    public void setNameFamilyMembers(final List<String> nameFamilyMembers) {
        this.nameFamilyMembers = nameFamilyMembers;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(final Expense expense) {
        this.expense = expense;
    }

    public Income getIncome() {
        return income;
    }

    public void setIncome(final Income income) {
        this.income = income;
    }
}

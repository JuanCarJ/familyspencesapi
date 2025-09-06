package com.familyspencesapi.domain.ranking;

import com.familyspencesapi.domain.expenseControl.Expense;

import java.util.List;
import java.util.UUID;

public class Ranking {

    private UUID idFamily;
    private List<String> nameFamilyMembers;
    private Expense expense;
    //private Income income;


    public Ranking(UUID idFamily, List<String> nameFamilyMembers, Expense expense) {
        this.idFamily = idFamily;
        this.nameFamilyMembers = nameFamilyMembers;
        this.expense = expense;
    }

    public UUID getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(final UUID idFamily) {
        this.idFamily = idFamily;
    }

    public List<String> getNameFamilyMembers() {
        return nameFamilyMembers;
    }

    public void setNameFamilyMembers(List<String> nameFamilyMembers) {
        this.nameFamilyMembers = nameFamilyMembers;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }
}

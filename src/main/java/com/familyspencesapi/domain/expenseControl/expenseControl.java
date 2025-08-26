package com.familyspencesapi.domain.expenseControl;

import com.familyspencesapi.domain.family.FamilyMember;
import java.math.BigDecimal;
import java.util.UUID;

public class Expense {

    private final UUID id; // UUID único del gasto
    private String title;
    private String description;
    private String period;
    private FamilyMember responsible; // relación con dominio Family
    private BigDecimal value;

    public Expense(String id, String title, String description, String period, FamilyMember responsible, BigDecimal value) {
        this.id = UUID.fromString(id);
        this.title = title;
        this.description = description;
        this.period = period;
        this.responsible = responsible;
        this.value = value;
    }

    // Getters y Setters (según necesidad)
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPeriod() {
        return period;
    }

    public FamilyMember getResponsible() {
        return responsible;
    }

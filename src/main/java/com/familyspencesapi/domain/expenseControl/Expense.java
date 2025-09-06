package com.familyspencesapi.domain.expenseControl;

import com.familyspencesapi.domain.family.FamilyMember;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Expense {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    @NotBlank
    @Size(min = 2, max = 50)
    private String period;

    @NotNull
    private FamilyMember responsible;

    @NotNull
    @Positive
    private BigDecimal value;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructor vacío
    public Expense() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor con ID (para datos quemados)
    public Expense(UUID id, String title, String description, String period, FamilyMember responsible, BigDecimal value) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.period = period;
        this.responsible = responsible;
        this.value = value;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor sin ID (para nuevos gastos)
    public Expense(String title, String description, String period, FamilyMember responsible, BigDecimal value) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.period = period;
        this.responsible = responsible;
        this.value = value;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
        this.updatedAt = LocalDateTime.now();
    }

    public FamilyMember getResponsible() {
        return responsible;
    }

    public void setResponsible(FamilyMember responsible) {
        this.responsible = responsible;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Métodos equals, hashCode y toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense)) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", period='" + period + '\'' +
                ", responsible=" + (responsible != null ? responsible.getName() : "null") +
                ", value=" + value +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

package com.familyspencesapi.domain.tasks;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.vacation.Vacation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "task")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID familyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    private UUID idResponsible;

    @OneToOne
    @JoinColumn(name = "vacation_id")
    private Vacation idVacations;

    @OneToOne
    @JoinColumn(name = "expenseve_id")
    private Expense idExpenseve;


    public Tasks() { // Noncompliant - method is empty
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(final boolean status) {
        this.status = status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public UUID getIdResponsible() {
        return idResponsible;
    }

    public void setIdResponsible(final UUID idResponsible) {
        this.idResponsible = idResponsible;
    }

    public Vacation getIdVacations() {
        return idVacations;
    }

    public void setIdVacations(final Vacation idVacations) {
        this.idVacations = idVacations;
    }
    public Expense getIdExpenseve() {
        return idExpenseve;
    }

    public void setIdExpenseve(final Expense idExpenseve) {
        this.idExpenseve = idExpenseve;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }
}



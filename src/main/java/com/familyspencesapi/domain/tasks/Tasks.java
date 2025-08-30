package com.familyspencesapi.domain.tasks;

import java.time.LocalDate;
import java.util.UUID;


public class Tasks {

    private UUID id;
    private UUID familiId;
    private String name;
    private String description;
    private boolean status;
    private LocalDate creationDate;
    private UUID idResponsible;
    private UUID idVacations;
    private UUID idExpenseve;

    public Tasks() {
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

    public UUID getIdVacations() {
        return idVacations;
    }

    public void setIdVacations(final UUID idVacations) {
        this.idVacations = idVacations;
    }

    public UUID getIdExpenseve() {
        return idExpenseve;
    }

    public void setIdExpenseve(final UUID idExpenseve) {
        this.idExpenseve = idExpenseve;
    }

    public UUID getFamiliId() {
        return familiId;
    }

    public void setFamiliId(UUID familiId) {
        this.familiId = familiId;
    }
}



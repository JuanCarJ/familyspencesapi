package com.familyspencesapi.domain.goals;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

//Domain Goal

@Entity
@Table(name = "goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_goal", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "family_id",nullable = false)
    @NotNull(message = "La familia no puede estar vacia")
    private UUID familyId;

    @Column(name = "name_goal", nullable = false, length = 150)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Column(name = "descripcion_goal", nullable = false, length = 500)
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @Column(name = "id_category", nullable = false)
    @NotNull(message = "La categoría no puede estar vacía")
    private UUID categoryId;

    @Column(name = "tope_goal", nullable = false)
    @Positive(message = "El tope debe ser mayor que 0")
    private double savingsCap;

    @FutureOrPresent(message = "La fecha límite debe ser igual o superior a la actual")
    @Column(name = "fecha_limite_goal", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "meta_diaria_goal", nullable = false)
    @Positive(message = "La meta diaria debe ser mayor que 0")
    private double dailyGoal;


    public Goal() {}

    public Goal(UUID id,UUID familyId, String name, String description, UUID categoryId, double savingsCap, LocalDateTime deadline, double dailyGoal) {
        this.id = id;
        this.familyId = familyId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.savingsCap = savingsCap;
        this.deadline = deadline;
        this.dailyGoal = dailyGoal;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFamilyId() {return familyId;}

    public void setFamilyId(UUID familyId) {this.familyId = familyId;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public double getSavingsCap() {
        return savingsCap;
    }

    public void setSavingsCap(double savingsCap) {
        this.savingsCap = savingsCap;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public double getDailyGoal() {
        return dailyGoal;
    }

    public void setDailyGoal(double dailyGoal) {
        this.dailyGoal = dailyGoal;
    }
}
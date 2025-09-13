package com.familyspencesapi.domain.goals;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idGoal", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "nameGoal", nullable = false, length = 150)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Column(name = "descripcionGoal", nullable = false, length = 500)
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    // Relación con Category (comentada por ahora)
    // @ManyToOne
    // @JoinColumn(name = "id", nullable = false)
    // private Category category;

    @Column(name = "topeGoal", nullable = false)
    @Positive(message = "El tope debe ser mayor que 0")
    private double savingsCap;

    @Column(name = "fechaLimiteGoal", nullable = false)
    @Future(message = "La fecha límite debe ser superior a la actual")
    private LocalDateTime deadline;

    @Column(name = "metaDiariaGoal", nullable = false)
    @Positive(message = "La meta diaria debe ser mayor que 0")
    private double dailyGoal;

    // Constructor vacío (obligatorio para JPA)
    public Goal() {}

    // Constructor con solo el ID
    public Goal(UUID id) {
        this.id = id;
    }

    // Constructor con parámetros (sin ID, porque se genera automáticamente)
    public Goal(String name, String description, double savingsCap, LocalDateTime deadline, double dailyGoal) {
        this.name = name;
        this.description = description;
        this.savingsCap = savingsCap;
        this.deadline = deadline;
        this.dailyGoal = dailyGoal;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    /*
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    */

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

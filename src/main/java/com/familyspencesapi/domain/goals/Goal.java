package com.familyspencesapi.domain.goals;

import com.familyspencesapi.domain.categories.Category;
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
    @Column(name = "id_goal", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(name = "name_goal", nullable = false, length = 150)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Column(name = "descripcion_goal", nullable = false, length = 500)
    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;


     @ManyToOne
     @JoinColumn(name = "id", nullable = false)
     private Category category;

    @Column(name = "tope_goal", nullable = false)
    @Positive(message = "El tope debe ser mayor que 0")
    private double savingsCap;

    @Column(name = "fecha_limite_goal", nullable = false)
    @Future(message = "La fecha límite debe ser superior a la actual")
    private LocalDateTime deadline;

    @Column(name = "meta_diaria_goal", nullable = false)
    @Positive(message = "La meta diaria debe ser mayor que 0")
    private double dailyGoal;


    public Goal() {}


    public Goal(UUID id, String name, String description, Category category, double savingsCap, LocalDateTime deadline, double dailyGoal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
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

     public Category getCategory() {
         return category;
     }

     public void setCategory(Category category) {
         this.category = category;
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

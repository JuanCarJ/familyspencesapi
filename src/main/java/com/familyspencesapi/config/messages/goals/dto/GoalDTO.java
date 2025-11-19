package com.familyspencesapi.config.messages.goals.dto;

import com.familyspencesapi.domain.goals.Goal;

import java.time.LocalDateTime;
import java.util.UUID;

public class GoalDTO {

    private UUID id;
    private UUID familyId;
    private UUID categoryId;
    private String name;
    private String description;
    private double savingsCap;
    private LocalDateTime deadline;
    private double dailyGoal;

    public GoalDTO() {
    }

    public GoalDTO(Goal goal) {
        this.id = goal.getId();
        this.familyId = goal.getFamilyId();
        this.categoryId = goal.getCategoryId();
        this.name = goal.getName();
        this.description = goal.getDescription();
        this.savingsCap = goal.getSavingsCap();
        this.deadline = goal.getDeadline();
        this.dailyGoal = goal.getDailyGoal();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
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

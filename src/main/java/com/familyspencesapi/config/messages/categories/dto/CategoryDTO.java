package com.familyspencesapi.config.messages.categories.dto;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategoryType;

import java.math.BigDecimal;
import java.util.UUID;

public class CategoryDTO {

    private UUID id;
    private UUID familyId;
    private String name;
    private CategoryType categoryType;
    private String description;
    private BigDecimal allocatedBudget;
    private BudgetPeriod budgetPeriod;

    public CategoryDTO() {
    }

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.familyId = category.getFamilyId();
        this.name = category.getName();
        this.categoryType = category.getCategoryType();
        this.description = category.getDescription();
        this.allocatedBudget = category.getAllocatedBudget();
        this.budgetPeriod = category.getBudgetPeriod();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAllocatedBudget() {
        return allocatedBudget;
    }

    public void setAllocatedBudget(BigDecimal allocatedBudget) {
        this.allocatedBudget = allocatedBudget;
    }

    public BudgetPeriod getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(BudgetPeriod budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }
}
package com.familyspencesapi.domain.categories;

import java.math.BigDecimal;
import java.util.UUID;

public class Category {

    private UUID id;
    private String name;
    private CategoryType categoryType;
    private String description;
    private BigDecimal allocatedBudget;
    private BudgetPeriod budgetPeriod;

    public Category() {
    }

    public Category(UUID id) {
        this.id = id;
    }

    public Category(UUID id, String name, CategoryType categoryType, String description, BigDecimal allocatedBudget, BudgetPeriod budgetPeriod) {
        this.id = id;
        this.name = name;
        this.categoryType = categoryType;
        this.description = description;
        this.allocatedBudget = allocatedBudget;
        this.budgetPeriod = budgetPeriod;
    }

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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryType=" + categoryType +
                ", description='" + description + '\'' +
                ", allocatedBudget=" + allocatedBudget +
                ", budgetPeriod=" + budgetPeriod +
                '}';
    }
}

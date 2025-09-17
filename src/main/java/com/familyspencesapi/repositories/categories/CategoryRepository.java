package com.familyspencesapi.repositories.categories;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByCategoryType(CategoryType categoryType);

    List<Category> findByBudgetPeriod(BudgetPeriod budgetPeriod);

    List<Category> findByCategoryTypeAndBudgetPeriod(CategoryType categoryType, BudgetPeriod budgetPeriod);

    boolean existsByNameIgnoreCase(String name);
}

package com.familyspencesapi.repositories.categories;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategoryType;
import com.familyspencesapi.domain.goals.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByCategoryType(CategoryType categoryType);
    List<Category> findByBudgetPeriod(BudgetPeriod budgetPeriod);
    List<Category> findByCategoryTypeAndBudgetPeriod(CategoryType categoryType, BudgetPeriod budgetPeriod);
    boolean existsByNameIgnoreCase(String name);

    List<Category> findByFamilyIdIsNull();
    List<Category> findByFamilyId(UUID familyId);

    boolean existsByNameIgnoreCaseAndFamilyIdIsNull(String name);
    boolean existsByNameIgnoreCaseAndFamilyId(String name, UUID familyId);

    @Query("SELECT c FROM Category c WHERE c.familyId IS NULL OR c.familyId = :familyId")
    List<Category> findGlobalAndFamilyCategories(@Param("familyId") UUID familyId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c " +
            "WHERE LOWER(c.name) = LOWER(:name) " +
            "AND (c.familyId IS NULL OR c.familyId = :familyId)")
    boolean existsByNameInContext(@Param("name") String name, @Param("familyId") UUID familyId);

    @Query("SELECT c FROM Category c WHERE " +
            "(c.familyId IS NULL OR c.familyId = :familyId) " +
            "AND (:type IS NULL OR c.categoryType = :type) " +
            "AND (:period IS NULL OR c.budgetPeriod = :period)")
    List<Category> findFilteredForFamily(
            @Param("familyId") UUID familyId,
            @Param("type") CategoryType type,
            @Param("period") BudgetPeriod period
    );

    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Goal g WHERE g.categoryId = :categoryId")
    boolean isUsedByGoal(@Param("categoryId") UUID categoryId);
}
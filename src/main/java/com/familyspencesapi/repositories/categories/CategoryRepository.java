package com.familyspencesapi.repositories.categories;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByFamilyId(UUID familyId);

    boolean existsByNameIgnoreCaseAndFamilyId(String name, UUID familyId);

    @Query("SELECT c FROM Category c WHERE " +
            "c.familyId = :familyId " +
            "AND (:type IS NULL OR c.categoryType = :type) " +
            "AND (:period IS NULL OR c.budgetPeriod = :period)")
    List<Category> findFilteredForFamily(
            @Param("familyId") UUID familyId,
            @Param("type") CategoryType type,
            @Param("period") BudgetPeriod period
    );
}
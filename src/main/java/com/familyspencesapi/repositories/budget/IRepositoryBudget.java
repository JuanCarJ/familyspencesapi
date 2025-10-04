package com.familyspencesapi.repositories.budget;

import com.familyspencesapi.domain.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IRepositoryBudget extends JpaRepository<Budget, UUID> {

    List<Budget> findByFamilyIdOrderByPeriod(UUID familyId);
}

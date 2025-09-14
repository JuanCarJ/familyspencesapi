package com.familyspencesapi.repositories.income;

import com.familyspencesapi.domain.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RepositoryIncome extends JpaRepository<Income, Long> {
    List<Income> findByFamilyId(UUID familyId);
}

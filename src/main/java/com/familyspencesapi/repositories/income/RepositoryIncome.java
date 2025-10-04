package com.familyspencesapi.repositories.income;

import com.familyspencesapi.domain.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepositoryIncome extends JpaRepository<Income, Long> {

    // Buscar ingresos por el ID de la familia
    List<Income> findByFamilyId(UUID familyId);


    List<Income> findByResponsibleId(UUID responsibleId);

}

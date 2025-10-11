package com.familyspencesapi.repositories.income;

import com.familyspencesapi.domain.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepositoryIncome extends JpaRepository<Income, UUID> {

    // Buscar ingresos por el ID de la familia
    List<Income> findByFamily(UUID family);

    // Buscar ingresos por el ID del responsable
    List<Income> findByResponsible_Id(UUID responsibleId);

    // Buscar ingresos por periodo
    List<Income> findByPeriod(String period);

    @Query("SELECT SUM(i.total) FROM Income i WHERE i.family = :familyId")
    Double sumTotalByFamilyId(UUID familyId);
}

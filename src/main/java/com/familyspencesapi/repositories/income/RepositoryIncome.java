package com.familyspencesapi.repositories.income;

import com.familyspencesapi.domain.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepositoryIncome extends JpaRepository<Income, UUID> {


    List<Income> findByFamily(UUID family);


    List<Income> findByResponsible_Id(UUID responsibleId);


    List<Income> findByPeriod(String period);

    List<Income> findByResponsibleIdAndPeriod(UUID responsibleId, String period);

    @Query("SELECT SUM(i.total) FROM Income i WHERE i.family = :familyId")
    Double sumTotalByFamilyId(UUID familyId);
}
package com.familyspencesapi.repositories.ranking;

import com.familyspencesapi.domain.ranking.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RankingRepository extends JpaRepository<Ranking, UUID> {

    List<Ranking> findByFamilyIdAndPeriodOrderByTotalExpenses(UUID familyId, String period);

    List<Ranking> findByFamilyIdAndPeriodOrderByTotalIncome(UUID familyId,String period);

    boolean existsByFamilyIdAndPeriod(UUID familyId, String period);

    void deleteByFamilyIdAndPeriod(UUID familyId, String period);

    @Modifying
    @Query("UPDATE Ranking r SET r.fullName = :newName WHERE r.userId = :userId")
    void updateFullNameByUserId(@Param("userId") UUID userId, @Param("newName") String newName);
}


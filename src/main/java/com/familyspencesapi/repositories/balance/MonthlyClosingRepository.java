package com.familyspencesapi.repositories.balance;

import com.familyspencesapi.domain.home.Closings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonthlyClosingRepository extends JpaRepository<Closings, UUID> {
    List<Closings> findByFamilyIdOrderByClosingDateDesc(UUID familyId);
}

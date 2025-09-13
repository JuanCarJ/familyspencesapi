package com.familyspencesapi.repositories.vacation;


import com.familyspencesapi.domain.vacation.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, UUID> {
}
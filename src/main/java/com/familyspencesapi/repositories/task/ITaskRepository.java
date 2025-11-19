package com.familyspencesapi.repositories.task;

import com.familyspencesapi.domain.tasks.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ITaskRepository extends JpaRepository<Tasks, UUID> {

    List<Tasks> findByFamilyId(UUID familyId);
}

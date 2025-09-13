package com.familyspencesapi.repositories.users;

import com.familyspencesapi.domain.users.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {
    Optional<Relationship> findByType(String type);
    boolean existsByType(String type);
}

package com.familyspencesapi.repositories.users;

import com.familyspencesapi.domain.users.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, UUID> {
    Optional<DocumentType> findByType(String type);
    boolean existsByType(String type);
}

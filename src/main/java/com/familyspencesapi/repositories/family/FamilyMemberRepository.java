package com.familyspencesapi.repositories.family;

import com.familyspencesapi.domain.family.FamilyMemberDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMemberDomain, UUID> {

    // Buscar por email
    Optional<FamilyMemberDomain> findByEmail(String email);

    // Buscar por nombre (case insensitive)
    List<FamilyMemberDomain> findByNameContainingIgnoreCase(String name);

    // Buscar por rol
    List<FamilyMemberDomain> findByRole(String role);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Verificar si existe un documento
    boolean existsByDocument(String document);

    // Buscar por nombre exacto
    Optional<FamilyMemberDomain> findByNameIgnoreCase(String name);
}

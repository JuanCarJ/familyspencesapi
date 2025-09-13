package com.familyspencesapi.repositories.family;

import com.familyspencesapi.domain.family.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyRepository extends JpaRepository<Family, UUID> {

    // Buscar familia por nombre exacto (ignorando mayúsculas y minúsculas)
    Optional<Family> findByNameIgnoreCase(String name);

    // Buscar familias que contengan el nombre (case insensitive)
    List<Family> findByNameContainingIgnoreCase(String name);

    // Verificar si ya existe una familia con este nombre
    boolean existsByNameIgnoreCase(String name);

    // Buscar familias por dirección (ejemplo adicional)
    List<Family> findByAddressContainingIgnoreCase(String address);
}

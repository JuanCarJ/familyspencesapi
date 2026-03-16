package com.familyspencesapi.repositories.pet;

import com.familyspencesapi.domain.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IRepositoryPet extends JpaRepository<Pet, UUID> {

    // Método para obtener todas las mascotas de una familia
    List<Pet> findByFamilyId(UUID familyId);
}
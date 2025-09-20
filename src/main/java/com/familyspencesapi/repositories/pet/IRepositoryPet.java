package com.familyspencesapi.repositories.pet;

import com.familyspencesapi.domain.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

//@Repository
public interface IRepositoryPet extends JpaRepository<Pet, UUID> {

}


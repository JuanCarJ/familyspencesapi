package com.familyspencesapi.service.pet;

import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.repositories.pet.IRepositoryPet;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PetService {

    private final IRepositoryPet petRepository;

    public PetService(IRepositoryPet petRepository) {
        this.petRepository = petRepository;
    }

    // Obtener todas las mascotas
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    // Buscar una mascota por su ID
    public Optional<Pet> getPetById(UUID id) {
        return petRepository.findById(id);
    }

    // Crear nueva mascota
    public Pet createPet(Pet pet) {
        // JPA generará automáticamente el ID
        return petRepository.save(pet);
    }

    // Eliminar mascota
    public boolean deletePet(UUID id) {
        if (petRepository.existsById(id)) {
            petRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Actualizar mascota
    public Pet updatePet(UUID id, Pet updatedPet) {
        return petRepository.findById(id).map(p -> {
            updatedPet.setId(id); // mantenemos el mismo ID
            return petRepository.save(updatedPet);
        }).orElse(null);
    }
}
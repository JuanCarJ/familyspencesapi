package com.familyspencesapi.service.pet;

import com.familyspencesapi.domain.pet.Pet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PetService {

    private final List<Pet> pets = new ArrayList<>();

    public PetService() {
        // Datos quemados de prueba
        pets.add(new Pet(UUID.randomUUID(), "Firulais Restrepo", "Dog", "Labrador", LocalDate.parse("2020-05-10"), UUID.randomUUID()));
        pets.add(new Pet(UUID.randomUUID(), "Misu López", "Cat", "Siamese", LocalDate.parse("2022-03-14"), UUID.randomUUID()));
    }

    // Obtener todas las mascotas
    public List<Pet> getAllPets() {
        return pets;
    }

    // Obtener mascota por ID
    public Optional<Pet> getPetById(UUID id) {
        return pets.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    // Crear mascota
    public Pet createPet(Pet pet) {
        pet.setId(UUID.randomUUID());
        pets.add(pet);
        return pet;
    }

    // Eliminar mascota
    public boolean deletePet(UUID id) {
        return pets.removeIf(p -> p.getId().equals(id));
    }

    public Pet updatePet(UUID id, Pet updatedPet) {
        return pets.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(p -> {
                    p.setFullName(updatedPet.getFullName());
                    p.setPetType(updatedPet.getPetType());
                    p.setBreed(updatedPet.getBreed());
                    p.setBirthDate(updatedPet.getBirthDate());
                    p.setFamilyId(updatedPet.getFamilyId());
                    return p;
                })
                .orElse(null);
    }


}

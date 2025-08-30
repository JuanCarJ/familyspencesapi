package com.familyspencesapi.controller;

import com.familyspencesapi.domain.Pet;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/rest")
public class PetController {

    private List<Pet> pets = new ArrayList<>();

    public PetController() {
        // Datos quemados
        pets.add(new Pet(UUID.randomUUID(), "Firulais Restrepo", "Dog", "Labrador", LocalDate.parse("2020-05-10"), UUID.randomUUID()));
        pets.add(new Pet(UUID.randomUUID(), "Misu López", "Cat", "Siamese", LocalDate.parse("2022-03-14"), UUID.randomUUID()));
    }

    // Obtener todas las mascotas
    @GetMapping("/pets")
    public List<Pet> getAllPets() {
        return pets;
    }

    // Obtener mascota por id
    @GetMapping("/pets/{id}")
    public Pet getPetById(@PathVariable UUID id) {
        return pets.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Crear mascota
    @PostMapping("/pets")
    public Pet createPet(@RequestBody Pet pet) {
        pet.setId(UUID.randomUUID());
        pets.add(pet);
        return pet;
    }

    // Eliminar mascota
    @DeleteMapping("/pets/{id}")
    public void deletePet(@PathVariable UUID id) {
        pets.removeIf(p -> p.getId().equals(id));
    }
}

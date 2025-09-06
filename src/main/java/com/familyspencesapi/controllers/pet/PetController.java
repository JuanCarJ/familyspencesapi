package com.familyspencesapi.controllers.pet;

import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.service.pet.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rest")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // Obtener todas las mascotas
    @GetMapping("/pets")
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    // Obtener mascota por id
    @GetMapping("/pets/{id}")
    public Optional<Pet> getPetById(@PathVariable UUID id) {
        return petService.getPetById(id);
    }

    // Crear mascota
    @PostMapping("/pets")
    public Pet createPet(@RequestBody Pet pet) {
        return petService.createPet(pet);
    }

    // Eliminar mascota
    @DeleteMapping("/pets/{id}")
    public boolean deletePet(@PathVariable UUID id) {
        return petService.deletePet(id);
    }

    // Actualizar mascota
    @PutMapping("/{id}")
    public Pet updatePet(@PathVariable UUID id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

}

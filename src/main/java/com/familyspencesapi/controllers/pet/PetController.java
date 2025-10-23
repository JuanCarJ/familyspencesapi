package com.familyspencesapi.controllers.pet;

import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.service.pet.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    // Obtener mascotas por familia
    @GetMapping("/pets/family")
    public ResponseEntity<Object> getPetsByFamily(@RequestParam String familyId) {
        try {
            List<Pet> familyPets = petService.getPetsByFamily(UUID.fromString(familyId));
            return ResponseEntity.ok(familyPets);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "Error interno del servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Crear mascota
    @PostMapping("/pets")
    public ResponseEntity<Object> createPet(@RequestBody Pet pet,
                                            @RequestParam String familyId) {
        try {
            Pet createdPet = petService.createPet(pet, familyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Eliminar mascota
    @DeleteMapping("/pets/{id}")
    public boolean deletePet(@PathVariable UUID id) {
        return petService.deletePet(id);
    }

    // Actualizar mascota
    @PutMapping("/pets/{id}")
    public ResponseEntity<Object> updatePet(@PathVariable UUID id,
                                            @RequestBody Pet pet,
                                            @RequestParam String familyId) {
        try {
            Pet updatedPet = petService.updatePet(id, pet, familyId);
            if (updatedPet != null) {
                return ResponseEntity.ok(updatedPet);
            }
            Map<String, String> errorResponse = Map.of("error", "Mascota no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
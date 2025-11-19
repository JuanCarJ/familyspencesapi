package com.familyspencesapi.controllers.pet;

import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.messages.pets.PetsMessageSender;
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
    private final PetsMessageSender petsMessageSender;
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_KEY = "error";

    public PetController(PetService petService, PetsMessageSender petsMessageSender) {
        this.petService = petService;
        this.petsMessageSender = petsMessageSender;
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    // Crear mascota
    @PostMapping("/pets")
    public ResponseEntity<Object> createPet(@RequestBody Pet pet,
                                            @RequestParam String familyId) {
        try {
            Pet createdPet = petService.createPet(pet, familyId);

            // Enviar mensaje a RabbitMQ
            petsMessageSender.sendPetCreated(createdPet);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    // Eliminar mascota
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Object> deletePet(@PathVariable UUID id,
                                            @RequestParam String familyId) {
        try {
            boolean deleted = petService.deletePet(id);

            if (deleted) {
                // Enviar mensaje a RabbitMQ
                petsMessageSender.sendPetDeleted(
                        Map.of("familyId", familyId, "petId", id.toString())
                );

                return ResponseEntity.ok(Map.of("message", "Pet deleted successfully"));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR_KEY, "Mascota no encontrada"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }

    // Actualizar mascota
    @PutMapping("/pets/{id}")
    public ResponseEntity<Object> updatePet(@PathVariable UUID id,
                                            @RequestBody Pet pet,
                                            @RequestParam String familyId) {
        try {
            Pet updatedPet = petService.updatePet(id, pet, familyId);

            if (updatedPet != null) {
                // Enviar mensaje a RabbitMQ
                petsMessageSender.sendPetUpdated(updatedPet);

                return ResponseEntity.ok(updatedPet);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR_KEY, "Mascota no encontrada"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(ERROR_KEY, UNEXPECTED_ERROR));
        }
    }
}
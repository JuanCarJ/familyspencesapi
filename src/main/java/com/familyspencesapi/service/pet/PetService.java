package com.familyspencesapi.service.pet;

import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.repositories.pet.IRepositoryPet;
import com.familyspencesapi.repositories.users.FamilyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PetService {

    private final IRepositoryPet petRepository;
    private final FamilyRepository familyRepository;
    private static final Logger log = LoggerFactory.getLogger(PetService.class);
    private static final String PET_NOT_FOUND = "Pet not found";
    private static final String FAMILY_NOT_FOUND = "Family not found";

    public PetService(IRepositoryPet petRepository, FamilyRepository familyRepository) {
        this.petRepository = petRepository;
        this.familyRepository = familyRepository;
    }

    // Obtener todas las mascotas
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    // Buscar una mascota por su ID
    public Optional<Pet> getPetById(UUID id) {
        return petRepository.findById(id);
    }

    // Obtener mascotas por familia
    public List<Pet> getPetsByFamily(UUID familyId) {
        validateAndGetFamily(familyId);
        return petRepository.findByFamilyId(familyId);
    }

    // Crear nueva mascota
    @Transactional
    public Pet createPet(Pet pet, String familyId) {
        log.info("Validando y creando pet...");

        validatePet(pet);
        UUID familyUUID = convertStringToUUID(familyId);
        validateAndGetFamily(familyUUID);

        // Asignar el familyId a la mascota
        pet.setFamilyId(familyUUID);

        Pet savedPet = petRepository.save(pet);
        log.info("Pet creada: {}", savedPet.getId());

        return savedPet;
    }

    // Eliminar mascota
    @Transactional
    public boolean deletePet(UUID id) {
        log.info("Validando y eliminando pet: {}", id);

        petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PET_NOT_FOUND));

        petRepository.deleteById(id);
        log.info("Pet eliminada: {}", id);

        return true;
    }

    // Actualizar mascota
    @Transactional
    public Pet updatePet(UUID id, Pet updatedPet, String familyId) {
        log.info("Validando y actualizando pet: {}", id);

        validatePet(updatedPet);
        UUID familyUUID = convertStringToUUID(familyId);
        validateAndGetFamily(familyUUID);

        Pet petToUpdate = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(PET_NOT_FOUND));

        petToUpdate.setFullName(updatedPet.getFullName());
        petToUpdate.setPetType(updatedPet.getPetType());
        petToUpdate.setBreed(updatedPet.getBreed());
        petToUpdate.setBirthDate(updatedPet.getBirthDate());
        petToUpdate.setFamilyId(familyUUID);

        Pet savedPet = petRepository.save(petToUpdate);
        log.info("Pet actualizada: {}", savedPet.getId());

        return savedPet;
    }

    // Métodos de validación
    private UUID convertStringToUUID(String familyIdString) {
        if (!StringUtils.hasText(familyIdString)) {
            throw new IllegalArgumentException("El ID de la familia no puede estar vacío");
        }

        try {
            return UUID.fromString(familyIdString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato de ID de familia inválido: " + familyIdString);
        }
    }

    private Family validateAndGetFamily(UUID familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("El ID de la familia no puede ser nulo");
        }

        return familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException(FAMILY_NOT_FOUND));
    }

    private void validatePet(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }

        if (!StringUtils.hasText(pet.getFullName())) {
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío");
        }

        if (pet.getFullName().length() < 2 || pet.getFullName().length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres");
        }

        if (!StringUtils.hasText(pet.getPetType())) {
            throw new IllegalArgumentException("El tipo de mascota no puede estar vacío");
        }

        if (!StringUtils.hasText(pet.getBreed())) {
            throw new IllegalArgumentException("La raza no puede estar vacía");
        }

        if (pet.getBirthDate() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }

        if (pet.getBirthDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
    }
}
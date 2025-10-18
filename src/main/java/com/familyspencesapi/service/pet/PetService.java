package com.familyspencesapi.service.pet;

import com.familyspencesapi.domain.pet.Pet;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.repositories.pet.IRepositoryPet;
import com.familyspencesapi.repositories.users.FamilyRepository;
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
        validatePet(pet);
        UUID familyUUID = convertStringToUUID(familyId);
        Family existingFamily = validateAndGetFamily(familyUUID);

        // Asignar el familyId a la mascota
        pet.setFamilyId(familyUUID);

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
    @Transactional
    public Pet updatePet(UUID id, Pet updatedPet, String familyId) {
        validatePet(updatedPet);
        UUID familyUUID = convertStringToUUID(familyId);
        validateAndGetFamily(familyUUID);

        return petRepository.findById(id).map(existingPet -> {
            updatedPet.setId(id);
            updatedPet.setFamilyId(familyUUID);
            return petRepository.save(updatedPet);
        }).orElse(null);
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
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));
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
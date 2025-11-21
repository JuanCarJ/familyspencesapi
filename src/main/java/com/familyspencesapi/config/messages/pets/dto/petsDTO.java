package com.familyspencesapi.config.messages.pets.dto;

import com.familyspencesapi.domain.pet.Pet;

import java.time.LocalDate;
import java.util.UUID;

public class petsDTO {
    private UUID id;
    private UUID familyId;
    private String fullName;
    private String petType;
    private String breed;
    private LocalDate birthDate;

    // Constructor vacío
    public petsDTO() {}

    // Constructor que convierte Pet a PetDTO
    public petsDTO(Pet pet) {
        this.id = pet.getId();
        this.familyId = pet.getFamilyId();
        this.fullName = pet.getFullName();
        this.petType = pet.getPetType();
        this.breed = pet.getBreed();
        this.birthDate = pet.getBirthDate();
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
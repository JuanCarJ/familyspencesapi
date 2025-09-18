package com.familyspencesapi.domain.pet;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "pets")
public class Pet {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;


    @Column(name = "full name", nullable = false, length = 100)
    private String fullName;


    @Column(name = "pet type", nullable = false, length = 100)
    private String petType;


    @Column(name = "breed", nullable = false, length = 100)
    private String breed;


    @Column(name = "birth date", nullable = false, length = 100)
    private LocalDate birthDate;


    @Column(name = "familyId", nullable = false, length = 100)
    private UUID familyId;



    // Constructor vacío
    public Pet() {
    }

    // Constructor con todos los atributos
    public Pet(UUID id, String fullName, String petType, String breed, LocalDate birthDate, UUID familyId) {
        this.id = id;
        this.fullName = fullName;
        this.petType = petType;
        this.breed = breed;
        this.birthDate = birthDate;
        this.familyId = familyId;
    }

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    // toString
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", petType='" + petType + '\'' +
                ", breed='" + breed + '\'' +
                ", birthDate=" + birthDate +
                ", familyId=" + familyId +
                '}';
    }
}


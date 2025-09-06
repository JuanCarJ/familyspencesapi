package com.familyspencesapi.domain.users;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

public class RegisterUser {
    private UUID id;
    private  String fullName;
    private LocalDate birthDate;
    private DocumentType documentType;
    private String document;
    private String email;
    private Relationship relationship;
    private String credit_card;
    private String phone;
    private String address;
    private String password;
    private UUID familyId;

    public RegisterUser( UUID id, String fullName, LocalDate birthDate, DocumentType documentType, String document, String email, Relationship relationship, String credit_card, String phone, String address, String password, UUID familyId) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.documentType = documentType;
        this.document = document;
        this.email = email;
        this.relationship = relationship;
        this.credit_card = credit_card;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.familyId = familyId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getfullName() {
        return fullName;
    }

    public void setfullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getbirthDate() {
        return birthDate;
    }

    public void setbirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public DocumentType getdocumentType() {
        return documentType;
    }

    public void setdocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getdocument() {
        return document;
    }

    public void setdocument(String document) {
        this.document = document;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getcredit_card() {
        return credit_card;
    }

    public void setcredit_card(String credit_card) {
        this.credit_card = credit_card;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getfamilyId() {
        return familyId;
    }

    public void setfamilyId(UUID familyId) {
        this.familyId = familyId;
    }
}

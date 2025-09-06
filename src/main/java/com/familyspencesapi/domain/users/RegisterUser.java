package com.familyspencesapi.domain.users;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user")
public class RegisterUser {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "full_name", nullable = false)
    private  String fullName;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;
    @Column(name = "document", nullable = false, unique = true)
    private String document;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @ManyToOne
    @JoinColumn(name = "relationship_id", nullable = false)
    private Relationship relationship;
    @Column(name = "credit_card", nullable = false)
    private String credit_card;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "password", nullable = false)
    private String password;
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "family_id", nullable = false)
    private UUID familyId;



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

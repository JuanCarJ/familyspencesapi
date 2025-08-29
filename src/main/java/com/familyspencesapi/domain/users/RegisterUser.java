package com.familyspencesapi.domain.users;

import java.time.LocalDate;
import java.util.UUID;

public class RegisterUser {
    private UUID id;
    private  String full_name;
    private LocalDate birth_date;
    private String document_type;
    private String document;
    private String email;
    private String relationship;
    private String credit_card;
    private String phone;
    private String address;
    private String password;
    private String id_family;


    public RegisterUser(UUID id, String full_name, LocalDate birth_date,
                        String document_type, String document,
                        String email, String relationship, String credit_card,
                        String phone, String address, String password, String id_family) {
        this.id = id;
        this.full_name = full_name;
        this.birth_date = birth_date;
        this.document_type = document_type;
        this.document = document;
        this.email = email;
        this.relationship = relationship;
        this.credit_card = credit_card;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.id_family = id_family;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getfull_name() {
        return full_name;
    }

    public void setfull_name(String full_name) {
        this.full_name = full_name;
    }

    public LocalDate getbirth_date() {
        return birth_date;
    }

    public void setbirth_date(LocalDate birth_date) {
        this.birth_date = birth_date;
    }

    public String getdocument_type() {
        return document_type;
    }

    public void setdocument_type(String document_type) {
        this.document_type = document_type;
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

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
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

    public String getid_family() {
        return id_family;
    }

    public void setid_family(String id_family) {
        this.id_family = id_family;
    }
}

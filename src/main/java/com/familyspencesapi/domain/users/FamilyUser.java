package com.familyspencesapi.domain.users;

import java.time.LocalDate;

public class FamilyUser {

    private String full_name;
    private LocalDate birth_date;
    private String document_type;
    private String document;
    private String email;
    private String relationship;
    private String credit_card;
    private String phone;
    private String address;

    public FamilyUser() {

    }

    public FamilyUser(String full_name, LocalDate birth_date, String document_type, String document, String email, String relationship, String credit_card, String phone, String address) {
        this.full_name = full_name;
        this.birth_date = birth_date;
        this.document_type = document_type;
        this.document = document;
        this.email = email;
        this.relationship = relationship;
        this.credit_card = credit_card;
        this.phone = phone;
        this.address = address;
    }

    public String getfull_name() {
        return full_name;
    }

    public void setfull_name(final String full_name) {
        this.full_name = full_name;
    }

    public LocalDate getbirth_date() {
        return birth_date;
    }

    public void setbirth_date(final LocalDate birth_date) {
        this.birth_date = birth_date;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(final String document_type) {
        this.document_type = document_type;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(final String document) {
        this.document = document;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(final String relationship) {
        this.relationship = relationship;
    }

    public String getCreditCard() {
        return credit_card;
    }

    public void setCreditCard(final String credit_card) {
        this.credit_card = credit_card;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }
}

package com.familyspencesapi.domain.users;

import java.time.LocalDate;

public class FamilyUser {

    private String name;
    private LocalDate birthDate;
    private String dniType;
    private String dni;
    private String email;
    private String relationShip;
    private String creditCard;
    private String cellphone;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDniType() {
        return dniType;
    }

    public void setDniType(final String dniType) {
        this.dniType = dniType;
    }

    public String getSni() {
        return dni;
    }

    public void setSni(final String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(final String relationShip) {
        this.relationShip = relationShip;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(final String creditCard) {
        this.creditCard = creditCard;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(final String cellphone) {
        this.cellphone = cellphone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }
}

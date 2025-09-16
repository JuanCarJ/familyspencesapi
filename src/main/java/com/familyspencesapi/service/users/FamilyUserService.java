package com.familyspencesapi.service.users;

import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.domain.users.RegisterUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.regex.Pattern;

@Service
public class FamilyUserService {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,100}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^3\\d{9}$");
    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("^\\d{13,19}$");





    // PATCH
    public RegisterUser updateUser(String email, RegisterUser updatedData) {
        RegisterUser optionalUser = new RegisterUser(
        );
        validate(updatedData);
        if (optionalUser.equals(null)) {
            return null;
        }

        RegisterUser existingUser = optionalUser;

        if (updatedData.getfullName() != null && !updatedData.getfullName().isBlank()) {
            existingUser.setFirstName(updatedData.getfullName());
        }
        if (updatedData.getLastName() != null && !updatedData.getLastName().isBlank()) {
            existingUser.setLastName(updatedData.getLastName());
        }

        if (updatedData.getdocumentType() != null && !updatedData.getdocumentType().equals(null)) {
            existingUser.setdocumentType(updatedData.getdocumentType());
        }
        if (updatedData.getdocument() != null && !updatedData.getdocument().isBlank()) {
            existingUser.setdocument(updatedData.getdocument());
        }
        if (updatedData.getcreditCard() != null && !updatedData.getcreditCard().isBlank()) {
            existingUser.setcreditCard(updatedData.getcreditCard());
        }
        if (updatedData.getphone() != null && !updatedData.getphone().isBlank()) {
            existingUser.setphone(updatedData.getphone());
        }
        if (updatedData.getAddress() != null && !updatedData.getAddress().isBlank()) {
            existingUser.setAddress(updatedData.getAddress());
        }

        return existingUser;
    }

    // PUT (actualización completa)
    public RegisterUser updateAllUser(String email, RegisterUser updatedUser) {
        RegisterUser optionalUser = new RegisterUser(
        );
        validate(updatedUser);
        if (optionalUser.equals(null)) {
            return null;
        }

        RegisterUser existingUser = optionalUser;
        existingUser.setFirstName(updatedUser.getfullName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setdocumentType(updatedUser.getdocumentType());
        existingUser.setdocument(updatedUser.getdocument());
        existingUser.setcreditCard(updatedUser.getcreditCard());
        existingUser.setphone(updatedUser.getphone());
        existingUser.setAddress(updatedUser.getAddress());

        return existingUser;
    }

    public void validate(RegisterUser user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        validateFullName(user.getfullName());
        validateDocumentType(user.getdocumentType());
        validateDocument(user.getdocument());
        validateCreditCard(user.getcreditCard());
        validatePhone(user.getphone());
        validateAddress(user.getAddress());
    }

    private void validateFullName(String fullName) {
        if (!StringUtils.hasText(fullName) || !NAME_PATTERN.matcher(fullName).matches()) {
            throw new IllegalArgumentException(
                    "El nombre debe tener entre 3 y 100 letras y espacios, sin números ni símbolos"
            );
        }
    }

    private void validateDocumentType(DocumentType type) {
        if (type == null || type.getId() == null) {
            throw new IllegalArgumentException("Tipo de documento invalido");
        }
    }
    private void validateDocument(String document) {
        if (!StringUtils.hasText(document)) {
            throw new IllegalArgumentException("El documento no puede estar vacio");
        }
        if (!document.matches("\\d{6,15}")) {
            throw new IllegalArgumentException("El documento debe tener entre 6 y 15 digitos");
        }
    }

    private void validateCreditCard(String creditCard) {
        if (!StringUtils.hasText(creditCard) || !CREDIT_CARD_PATTERN.matcher(creditCard).matches()) {
            throw new IllegalArgumentException("Numero de tarjeta de credito invalido (13-19 digitos)");
        }
    }

    private void validatePhone(String phone) {
        if (!StringUtils.hasText(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Formato de telefono invalido");
        }
    }

    private void validateAddress(String address) {
        if (!StringUtils.hasText(address) || address.length() < 5) {
            throw new IllegalArgumentException("La direccion no puede estar vacia y debe tener minimo 5 caracteres");
        }
    }
}


package com.familyspencesapi.service.users;

import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.domain.users.FamilyUser;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.domain.users.Relationship;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FamilyUserService {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,100}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^3\\d{9}$");
    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("^\\d{13,19}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).{8,}$");

    private static final List<DocumentType> DOCUMENT_TYPES = List.of(
            new DocumentType(UUID.fromString("11111111-1111-1111-1111-111111111111"), "CC"),
            new DocumentType(UUID.fromString("22222222-2222-2222-2222-222222222222"), "TI"),
            new DocumentType(UUID.fromString("33333333-3333-3333-3333-333333333333"), "CE")
    );

    private static final List<Relationship> RELATIONSHIPS = List.of(
            new Relationship(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "PADRE"),
            new Relationship(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "HIJO"),
            new Relationship(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), "TUTOR")
    );
    // GET user by email
    public RegisterUser getUserByEmail(String email) {

        return new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                new DocumentType(UUID.randomUUID(), "CC"),
                "1234567890",
                "carlos.perez@example.com",
                new Relationship(UUID.randomUUID(), "Hijo"),
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                UUID.fromString("11111111-1111-1111-1111-111111111111")
        );
    }

    // PATCH
    public RegisterUser updateUser(String email, RegisterUser updatedData) {
        RegisterUser optionalUser = new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                new DocumentType(UUID.randomUUID(), "CC"),
                "1234567890",
                "carlos.perez@example.com",
                new Relationship(UUID.randomUUID(), "Hijo"),
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                UUID.fromString("11111111-1111-1111-1111-111111111111")
        );
        validate(updatedData);
        if (optionalUser.equals(null)) {
            return null;
        }

        RegisterUser existingUser = optionalUser;

        if (updatedData.getfullName() != null && !updatedData.getfullName().isBlank()) {
            existingUser.setfullName(updatedData.getfullName());
        }
        if (updatedData.getdocumentType() != null && !updatedData.getdocumentType().equals(null)) {
            existingUser.setdocumentType(updatedData.getdocumentType());
        }
        if (updatedData.getdocument() != null && !updatedData.getdocument().isBlank()) {
            existingUser.setdocument(updatedData.getdocument());
        }
        if (updatedData.getcredit_card() != null && !updatedData.getcredit_card().isBlank()) {
            existingUser.setcredit_card(updatedData.getcredit_card());
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
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                new DocumentType(UUID.randomUUID(), "CC"),
                "1234567890",
                "carlos.perez@example.com",
                new Relationship(UUID.randomUUID(), "Hijo"),
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                UUID.fromString("11111111-1111-1111-1111-111111111111")
        );
        validate(updatedUser);
        if (optionalUser.equals(null)) {
            return null;
        }

        RegisterUser existingUser = optionalUser;
        existingUser.setfullName(updatedUser.getfullName());
        existingUser.setdocumentType(updatedUser.getdocumentType());
        existingUser.setdocument(updatedUser.getdocument());
        existingUser.setcredit_card(updatedUser.getcredit_card());
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
        validateCreditCard(user.getcredit_card());
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


package com.familyspencesapi.service.users;

import com.familyspencesapi.domain.users.FamilyUser;
import com.familyspencesapi.domain.users.RegisterUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyUserService {

    // GET user by email
    public RegisterUser getUserByEmail(String email) {

        return new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                "CC",
                "1234567890",
                "carlos.perez@example.com",
                "Hijo",
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                "FAM-001"
        );
    }

    // PATCH
    public RegisterUser updateUser(String email, RegisterUser updatedData) {
        RegisterUser optionalUser = new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                "CC",
                "1234567890",
                "carlos.perez@example.com",
                "Hijo",
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                "FAM-001"
        );
        if (optionalUser.equals(null)) {
            return null;
        }

        RegisterUser existingUser = optionalUser;

        if (updatedData.getfull_name() != null && !updatedData.getfull_name().isBlank()) {
            existingUser.setfull_name(updatedData.getfull_name());
        }
        if (updatedData.getdocument_type() != null && !updatedData.getdocument_type().isBlank()) {
            existingUser.setdocument_type(updatedData.getdocument_type());
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
    public RegisterUser updateAllUser(String email, FamilyUser updatedUser) {
        RegisterUser optionalUser = new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                "CC",
                "1234567890",
                "carlos.perez@example.com",
                "Hijo",
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                "FAM-001"
        );;
        if (optionalUser.equals(null)) {
            return null;
        }

        RegisterUser existingUser = optionalUser;
        existingUser.setfull_name(updatedUser.getfull_name());
        existingUser.setdocument_type(updatedUser.getDocument_type());
        existingUser.setdocument(updatedUser.getDocument());
        existingUser.setcredit_card(updatedUser.getCreditCard());
        existingUser.setphone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());

        return existingUser;
    }
}


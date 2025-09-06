//package com.familyspencesapi.service.users;
//
//import com.familyspencesapi.domain.users.DocumentType;
//import com.familyspencesapi.domain.users.FamilyUser;
//import com.familyspencesapi.domain.users.RegisterUser;
//import com.familyspencesapi.domain.users.Relationship;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class FamilyUserService {
//
//    // GET user by email
//    public RegisterUser getUserByEmail(String email) {
//
//        return new RegisterUser(
//                UUID.randomUUID(),
//                "Carlos Pérez",
//                LocalDate.of(1995, 5, 12),
//                new DocumentType(UUID.randomUUID(), "CC"),
//                "1234567890",
//                "carlos.perez@example.com",
//                new Relationship(UUID.randomUUID(), "Hijo"),
//                "4111111111111111",
//                "3001234567",
//                "Calle 123 #45-67, Bogotá",
//                "segura123",
//                UUID.fromString("11111111-1111-1111-1111-111111111111")
//        );
//    }
//
//    // PATCH
//    public RegisterUser updateUser(String email, RegisterUser updatedData) {
//        RegisterUser optionalUser = new RegisterUser(
//                UUID.randomUUID(),
//                "Carlos Pérez",
//                LocalDate.of(1995, 5, 12),
//                new DocumentType(UUID.randomUUID(), "CC"),
//                "1234567890",
//                "carlos.perez@example.com",
//                new Relationship(UUID.randomUUID(), "Hijo"),
//                "4111111111111111",
//                "3001234567",
//                "Calle 123 #45-67, Bogotá",
//                "segura123",
//                UUID.fromString("11111111-1111-1111-1111-111111111111")
//        );
//        if (optionalUser.equals(null)) {
//            return null;
//        }
//
//        RegisterUser existingUser = optionalUser;
//
//        if (updatedData.getfullName() != null && !updatedData.getfullName().isBlank()) {
//            existingUser.setfullName(updatedData.getfullName());
//        }
//        if (updatedData.getdocumentType() != null && !updatedData.getdocumentType().equals(null)) {
//            existingUser.setdocumentType(updatedData.getdocumentType());
//        }
//        if (updatedData.getdocument() != null && !updatedData.getdocument().isBlank()) {
//            existingUser.setdocument(updatedData.getdocument());
//        }
//        if (updatedData.getcredit_card() != null && !updatedData.getcredit_card().isBlank()) {
//            existingUser.setcredit_card(updatedData.getcredit_card());
//        }
//        if (updatedData.getphone() != null && !updatedData.getphone().isBlank()) {
//            existingUser.setphone(updatedData.getphone());
//        }
//        if (updatedData.getAddress() != null && !updatedData.getAddress().isBlank()) {
//            existingUser.setAddress(updatedData.getAddress());
//        }
//
//        return existingUser;
//    }
//
//    // PUT (actualización completa)
//    public RegisterUser updateAllUser(String email, RegisterUser updatedUser) {
//        RegisterUser optionalUser = new RegisterUser(
//                UUID.randomUUID(),
//                "Carlos Pérez",
//                LocalDate.of(1995, 5, 12),
//                new DocumentType(UUID.randomUUID(), "CC"),
//                "1234567890",
//                "carlos.perez@example.com",
//                new Relationship(UUID.randomUUID(), "Hijo"),
//                "4111111111111111",
//                "3001234567",
//                "Calle 123 #45-67, Bogotá",
//                "segura123",
//                UUID.fromString("11111111-1111-1111-1111-111111111111")
//        );
//        if (optionalUser.equals(null)) {
//            return null;
//        }
//
//        RegisterUser existingUser = optionalUser;
//        existingUser.setfullName(updatedUser.getfullName());
//        existingUser.setdocumentType(updatedUser.getdocumentType());
//        existingUser.setdocument(updatedUser.getdocument());
//        existingUser.setcredit_card(updatedUser.getcredit_card());
//        existingUser.setphone(updatedUser.getphone());
//        existingUser.setAddress(updatedUser.getAddress());
//
//        return existingUser;
//    }
//}


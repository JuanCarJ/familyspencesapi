//package com.familyspencesapi.controllers.users;
//
//import com.familyspencesapi.domain.users.DocumentType;
//import com.familyspencesapi.domain.users.FamilyUser;
//import com.familyspencesapi.domain.users.RegisterUser;
//import com.familyspencesapi.domain.users.Relationship;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api")
//public class FamilyUserController {
//
//    @GetMapping(value = "/users/profile", produces = "application/json")
//    public ResponseEntity<?> getUser(@RequestParam String email) {
//        if (email == null || email.isBlank()) {
//            return ResponseEntity.badRequest().build();
//        }
//        RegisterUser myUser = new RegisterUser(
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
//        return ResponseEntity.ok(myUser);
//    }
//
//    @PatchMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
//    public ResponseEntity<?> updateUser(
//            @PathVariable String email,
//            @RequestBody RegisterUser updatedData) {
//
//        RegisterUser existingUser = new RegisterUser(
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
//
//        if (existingUser == null) {
//            return ResponseEntity.notFound().build();
//        }
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
//        return ResponseEntity.ok(existingUser);
//    }
//
//    //Put agragdo para actualizar todo el perfil
//    @PutMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
//    public ResponseEntity<?> updateAllUser(
//            @PathVariable String email,
//            @RequestBody RegisterUser updatedUser) {
//
//        RegisterUser existingUser = new RegisterUser(
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
//
//        if (existingUser == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        existingUser.setfullName(updatedUser.getfullName());
//        existingUser.setdocumentType(updatedUser.getdocumentType());
//        existingUser.setcredit_card(updatedUser.getcredit_card());
//        existingUser.setphone(updatedUser.getphone());
//        existingUser.setAddress(updatedUser.getAddress());
//
//        return ResponseEntity.ok(existingUser);
//    }
//}

package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.FamilyUser;
import com.familyspencesapi.domain.users.RegisterUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FamilyUserController {

    @GetMapping(value = "/users/profile", produces = "application/json")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        RegisterUser myUser = new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                "CC",
                "1234567890",
                email,
                "Hijo",
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                "FAM-001"
        );
        return ResponseEntity.ok(myUser);
    }

    @PatchMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUser(
            @PathVariable String email,
            @RequestBody RegisterUser updatedData) {

        RegisterUser existingUser = new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                "CC",
                "1234567890",
                email,
                "Hijo",
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                "FAM-001"
        );

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

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

        return ResponseEntity.ok(existingUser);
    }

    //Put agragdo para actualizar todo el perfil
    @PutMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateAllUser(
            @PathVariable String email,
            @RequestBody RegisterUser updatedUser) {

        RegisterUser existingUser = new RegisterUser(
                UUID.randomUUID(),
                "Carlos Pérez",
                LocalDate.of(1995, 5, 12),
                "CC",
                "1234567890",
                email,
                "Hijo",
                "4111111111111111",
                "3001234567",
                "Calle 123 #45-67, Bogotá",
                "segura123",
                "FAM-001"
        );

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setfull_name(updatedUser.getfull_name());
        existingUser.setdocument_type(updatedUser.getdocument_type());
        existingUser.setcredit_card(updatedUser.getcredit_card());
        existingUser.setphone(updatedUser.getphone());
        existingUser.setAddress(updatedUser.getAddress());

        return ResponseEntity.ok(existingUser);
    }
}

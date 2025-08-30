package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.FamilyUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FamilyUserController {

    @GetMapping(value = "/users/profile", produces = "application/json")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        FamilyUser myUser = new FamilyUser();
        myUser.setEmail(email);
        myUser.setfull_name("Usuario de prueba");
        return ResponseEntity.ok(myUser);
    }

    @PatchMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUser(
            @PathVariable String email,
            @RequestBody FamilyUser updatedData) {

        FamilyUser existingUser = new FamilyUser();
        existingUser.setEmail(email);
        existingUser.setfull_name("Old Name");

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (updatedData.getfull_name() != null && !updatedData.getfull_name().isBlank()) {
            existingUser.setfull_name(updatedData.getfull_name());
        }
        if (updatedData.getDocument_type() != null && !updatedData.getDocument_type().isBlank()) {
            existingUser.setDocument_type(updatedData.getDocument_type());
        }
        if (updatedData.getDocument() != null && !updatedData.getDocument().isBlank()) {
            existingUser.setDocument(updatedData.getDocument());
        }
        if (updatedData.getCreditCard() != null && !updatedData.getCreditCard().isBlank()) {
            existingUser.setCreditCard(updatedData.getCreditCard());
        }
        if (updatedData.getPhone() != null && !updatedData.getPhone().isBlank()) {
            existingUser.setPhone(updatedData.getPhone());
        }
        if (updatedData.getAddress() != null && !updatedData.getAddress().isBlank()) {
            existingUser.setAddress(updatedData.getAddress());
        }

        return ResponseEntity.ok(existingUser);
    }


    @PutMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateAllUser(
            @PathVariable String email,
            @RequestBody FamilyUser updatedUser) {

        FamilyUser existingUser = new FamilyUser();
        existingUser.setEmail(email);
        existingUser.setfull_name("Old Name");

        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        existingUser.setfull_name(updatedUser.getfull_name());
        existingUser.setDocument_type(updatedUser.getDocument_type());
        existingUser.setCreditCard(updatedUser.getCreditCard());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAddress(updatedUser.getAddress());

        return ResponseEntity.ok(existingUser);
    }
}

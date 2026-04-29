package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.service.users.FamilyUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class FamilyUserController {

    private FamilyUserService userService;

    public FamilyUserController(FamilyUserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users/profile", produces = "application/json")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        RegisterUser myUser = userService.getUserByEmail(email);
        if (myUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(myUser);
    }

    @GetMapping(value = "/users/by-id/{userId}", produces = "application/json")
    public ResponseEntity<?> getUserById(@PathVariable UUID userId) {
        RegisterUser myUser = userService.getUserById(userId);
        if (myUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(myUser);
    }

    @PatchMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUser(
            @PathVariable String email,
            @RequestBody RegisterUser updatedData) {
        try {
            RegisterUser updatedUser = userService.updateUser(email, updatedData);
            if (updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateAllUser(
            @PathVariable String email,
            @RequestBody RegisterUser updatedUser) {
        try {
            RegisterUser savedUser = userService.updateAllUser(email, updatedUser);
            if (savedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/users/{email}/change-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> changePassword(
            @PathVariable String email,
            @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(email, request.currentPassword(), request.newPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping(value = "/users/by-id/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUserById(
            @PathVariable UUID id,
            @RequestBody RegisterUser updatedUser) {
        try {
            userService.updateUserById(id, updatedUser);
            return ResponseEntity.ok(Map.of("message", "Actualización enviada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = "/users/by-id/{id}", produces = "application/json")
    public ResponseEntity<?> deactivateUser(@PathVariable UUID id) {
        try {
            userService.deactivateUserById(id);
            return ResponseEntity.ok(Map.of("message", "Solicitud de desactivación enviada."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = "/users/{email}", produces = "application/json")
    public ResponseEntity<?> deleteAccount(@PathVariable String email) {
        try {
            userService.deleteAccount(email);
            return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    public record ChangePasswordRequest(String currentPassword, String newPassword) {}
}

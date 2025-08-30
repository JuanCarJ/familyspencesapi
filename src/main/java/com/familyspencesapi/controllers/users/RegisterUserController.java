package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.RegisterUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/users/register")
public class RegisterUserController {

    private final List<RegisterUser> users = new ArrayList<>();

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUser user) {
        if (user == null ||
                user.getfullName() == null || user.getfullName().isEmpty() ||
                user.getbirthDate() == null ||
                user.getdocumentType() == null ||
                user.getdocument() == null || user.getdocument().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getRelationship() == null ||
                user.getcredit_card() == null || user.getcredit_card().isEmpty() ||
                user.getphone() == null || user.getphone().isEmpty() ||
                user.getAddress() == null || user.getAddress().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios.");
        }
        user.setId(UUID.randomUUID());
        user.setfamilyId(UUID.randomUUID());
        users.add(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public RegisterUser getUser(@PathVariable UUID id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @GetMapping
    public List<RegisterUser> getAllUsers() {
        return users;
    }
}
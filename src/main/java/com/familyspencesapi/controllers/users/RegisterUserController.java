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

    public RegisterUserController() {
        users.add(new RegisterUser(
                UUID.randomUUID(),
                "Brahian R",
                LocalDate.of(2004, 1, 20),
                "Cedula",
                "12345678",
                "brahianr@email.com",
                "Padre",
                "123456789",
                "1234567890",
                "Rionegro",
                "password123",
                UUID.randomUUID().toString()
        ));
        users.add(new RegisterUser(
                UUID.randomUUID(),
                "Ana ",
                LocalDate.of(1985, 8, 15),
                "Cedula",
                "87654321",
                "ana@email.com",
                "Madre",
                "4222222222222222",
                "5557654321",
                "Rionegro",
                "password456",
                UUID.randomUUID().toString()
        ));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUser user) {
        if (user == null ||
                user.getfull_name() == null || user.getfull_name().isEmpty() ||
                user.getbirth_date() == null ||
                user.getdocument_type() == null || user.getdocument_type().isEmpty() ||
                user.getdocument() == null || user.getdocument().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getRelationship() == null || user.getRelationship().isEmpty() ||
                user.getcredit_card() == null || user.getcredit_card().isEmpty() ||
                user.getphone() == null || user.getphone().isEmpty() ||
                user.getAddress() == null || user.getAddress().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios.");
        }
        user.setId(UUID.randomUUID());
        user.setid_family(UUID.randomUUID().toString());
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
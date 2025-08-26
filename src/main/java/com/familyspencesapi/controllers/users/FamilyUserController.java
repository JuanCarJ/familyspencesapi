package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.FamilyUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FamilyUserController {

    @GetMapping(value = "/users/myuser", produces = "application/json")
    public ResponseEntity<?> myUser(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        FamilyUser myUser = new FamilyUser();
        myUser.setEmail(email);
        myUser.setfull_name("Usuario de prueba");
        return ResponseEntity.ok(new FamilyUser());
    }
}

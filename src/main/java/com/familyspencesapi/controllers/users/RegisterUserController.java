package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.services.users.RegisterUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/register")
public class RegisterUserController {

    private final RegisterUserService registerUserService;

    public RegisterUserController(RegisterUserService registerUserService) {
        this.registerUserService = registerUserService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterUser user) {
        try {
            RegisterUser createdUser = registerUserService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
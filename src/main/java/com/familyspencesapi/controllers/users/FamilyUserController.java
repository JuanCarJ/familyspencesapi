package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.service.users.FamilyUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateUser(
            @PathVariable String email,
            @RequestBody RegisterUser updatedData) {

        RegisterUser updatedUser = userService.updateUser(email, updatedData);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping(value = "/users/{email}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> updateAllUser(
            @PathVariable String email,
            @RequestBody RegisterUser updatedUser) {

        RegisterUser savedUser = userService.updateAllUser(email, updatedUser);
        if (savedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(savedUser);
    }
}

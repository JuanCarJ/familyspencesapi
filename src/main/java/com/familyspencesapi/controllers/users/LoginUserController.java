package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginUserController {

    @PostMapping(value = "/users/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginUser body) {
        if (body == null || isBlank(body.getEmail()) || isBlank(body.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(Map.of("idfamily", "123456"));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
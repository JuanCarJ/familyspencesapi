package com.familyspencesapi.controllers.users;

import com.familyspencesapi.domain.users.LoginUser;
import com.familyspencesapi.service.users.LoginUserService;
import com.familyspencesapi.utils.LoginUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginUserController {

    private final LoginUserService loginService;

    public LoginUserController(LoginUserService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/users/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> login(@RequestBody LoginUser loginRequest) {
        try {
            String token = loginService.authenticate(loginRequest);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (LoginUserException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public record AuthResponse(String token) {}
}

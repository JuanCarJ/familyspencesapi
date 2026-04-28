package com.familyspencesapi.controllers.family;

import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.service.family.FamilyMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/family")
public class FamilyMemberController {

    private static final String ERROR_KEY = "error";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Error interno del servidor. Intenta nuevamente.";

    private final FamilyMemberService familyMemberService;

    public FamilyMemberController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }

    @PostMapping("/members")
    public ResponseEntity<Object> createFamilyMember(@RequestBody RegisterUser newUser,
                                                     @RequestParam String familyId) {
        try {
            RegisterUser savedUser = familyMemberService.createUser(newUser, familyId);
            Map<String, Object> successResponse = Map.of(
                    "message", "Usuario creado exitosamente.",
                    "status", "PENDING",
                    "userId", savedUser.getId()
            );
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(successResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    @GetMapping("/members")
    public ResponseEntity<Object> getFamilyMembers(@RequestParam String familyId) {
        try {
            List<RegisterUser> familyMembers = familyMemberService.getFamilyMembers(
                    UUID.fromString(familyId)
            );
            return ResponseEntity.ok(familyMembers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(ERROR_KEY, INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }
}
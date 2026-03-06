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

    private final FamilyMemberService familyMemberService;

    public FamilyMemberController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }

    @PostMapping("/members")
    public ResponseEntity<Object> createFamilyMember(@RequestBody RegisterUser newUser,
                                                     @RequestParam String familyId) {
        try {

            String result = familyMemberService.createUser(newUser, familyId);
            Map<String, String> successResponse = Map.of(
                    "message", result,
                    "status", "PENDING"
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(successResponse);

        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = Map.of("error", "Error al conectar con el servidor de mensajería. Intenta nuevamente.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
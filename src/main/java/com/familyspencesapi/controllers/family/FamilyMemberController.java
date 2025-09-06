package com.familyspencesapi.controllers.family;

import com.familyspencesapi.domain.family.FamilyMemberDomain;
import com.familyspencesapi.service.family.FamilyMemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rest")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    public FamilyMemberController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }

    // Get all family members
    @GetMapping("/family-members")
    public List<FamilyMemberDomain> getAllFamilyMembers() {
        return familyMemberService.getAllFamilyMembers();
    }

    @GetMapping("/family-members/{id}")
    public Optional<FamilyMemberDomain> getFamilyMemberById(@PathVariable UUID id) {
        return familyMemberService.getFamilyMemberById(id);
    }

    @PostMapping("/family-members")
    public FamilyMemberDomain createFamilyMember(@RequestBody FamilyMemberDomain member) {
        return familyMemberService.createFamilyMember(member);
    }

    @DeleteMapping("/family-members/{id}")
    public boolean deleteFamilyMember(@PathVariable UUID id) {
        return familyMemberService.deleteFamilyMember(id);
    }

    @PutMapping("/family-members/{id}")
    public FamilyMemberDomain updateFamilyMember(@PathVariable UUID id, @RequestBody FamilyMemberDomain member) {
        return familyMemberService.updateFamilyMember(id, member);
    }
}

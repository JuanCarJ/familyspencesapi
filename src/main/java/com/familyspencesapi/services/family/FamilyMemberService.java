package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.family.AddFamilyMemberDomain;
import com.familyspencesapi.domain.family.FamilyMemberDomain;

import java.util.List;
import java.util.UUID;

public interface FamilyMemberService {
    FamilyMemberDomain addFamilyMember(AddFamilyMemberDomain newMember);
    List<FamilyMemberDomain> getAllFamilyMembers();
    FamilyMemberDomain getFamilyMemberById(UUID id);
    FamilyMemberDomain updateFamilyMember(UUID id, FamilyMemberDomain member);
    void deleteFamilyMember(UUID id);
}

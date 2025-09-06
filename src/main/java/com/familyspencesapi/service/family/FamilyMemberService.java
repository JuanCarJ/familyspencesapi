package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.family.FamilyMemberDomain;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FamilyMemberService {

    private final Map<UUID, FamilyMemberDomain> familyMembers = new HashMap<>();

    public List<FamilyMemberDomain> getAllFamilyMembers() {
        return new ArrayList<>(familyMembers.values());
    }

    public Optional<FamilyMemberDomain> getFamilyMemberById(UUID id) {
        return Optional.ofNullable(familyMembers.get(id));
    }

    public FamilyMemberDomain createFamilyMember(FamilyMemberDomain member) {
        familyMembers.put(member.getId(), member);
        return member;
    }

    public boolean deleteFamilyMember(UUID id) {
        return familyMembers.remove(id) != null;
    }

    public FamilyMemberDomain updateFamilyMember(UUID id, FamilyMemberDomain member) {
        if (familyMembers.containsKey(id)) {
            member.setId(id);
            familyMembers.put(id, member);
            return member;
        }
        return null;
    }
}

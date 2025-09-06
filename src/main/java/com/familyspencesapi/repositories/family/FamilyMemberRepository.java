package com.familyspencesapi.repositories.family;

import com.familyspencesapi.domain.family.FamilyMemberDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMemberDomain, UUID> {
}

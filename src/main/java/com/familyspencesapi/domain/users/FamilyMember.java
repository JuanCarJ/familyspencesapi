package com.familyspencesapi.domain.users;

import java.time.LocalDate;
import java.util.UUID;

public record FamilyMember(
        UUID id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        UUID documentTypeId,
        String document,
        String email,
        UUID relationshipId,
        String creditCard,
        String phone,
        String address,
        String password,
        UUID familyId
) {

}

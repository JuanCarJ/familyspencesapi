package com.familyspencesapi.domain.users;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateUserMessage(
    UUID id,
    String firstName,
    String lastName,
    LocalDate birthDate,
    UUID documentTypeId,
    String document,
    UUID relationshipId,
    String creditCard,
    String phone,
    String address
) {}

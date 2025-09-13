package com.familyspencesapi.repositories.users;

import com.familyspencesapi.domain.users.RegisterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegisterUserRepository extends JpaRepository<RegisterUser, UUID> {
    boolean existsByEmail(String email);
    boolean existsByDocument(String document);


}

package com.familyspencesapi.repositories.users;

import com.familyspencesapi.domain.users.RegisterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegisterUserRepository extends JpaRepository<RegisterUser, UUID> {

    /**
     * Verificar si existe un usuario con el email especificado
     */
    boolean existsByEmail(String email);

    /**
     * Verificar si existe un usuario con el documento especificado
     */
    boolean existsByDocument(String document);

    /**
     * Buscar usuarios por ID de familia - NUEVO MÉTODO PARA EXPENSE CONTROLLER
     */
    @Query("SELECT u FROM RegisterUser u WHERE u.family.id = :familyId")
    List<RegisterUser> findByFamilyId(@Param("familyId") UUID familyId);

    /**
     * Buscar usuario por email - retorna Optional para FamilyUserService
     */
    @Query("SELECT u FROM RegisterUser u WHERE u.email = :email")
    Optional<RegisterUser> findByEmail(@Param("email") String email);

    /**
     * Buscar usuario por documento - retorna Optional
     */
    @Query("SELECT u FROM RegisterUser u WHERE u.document = :document")
    Optional<RegisterUser> findByDocument(@Param("document") String document);
}
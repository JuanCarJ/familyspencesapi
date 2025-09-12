package com.familyspencesapi.repositories.family;

<<<<<<< HEAD
import com.familyspencesapi.domain.family.FamilyMemberDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FamilyMemberRepository extends JpaRepository<FamilyMemberDomain, UUID> {
}
=======
import com.familyspencesapi.domain.family.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, UUID> {

    // Buscar por email
    Optional<FamilyMember> findByEmail(String email);

    // Buscar por nombre (case insensitive)
    List<FamilyMember> findByNameContainingIgnoreCase(String name);

    // Buscar por rol
    List<FamilyMember> findByRole(String role);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar miembros activos (si tienes un campo de estado)
    // List<FamilyMember> findByActiveTrue();

    // Buscar por nombre exacto
    Optional<FamilyMember> findByNameIgnoreCase(String name);

    // Contar miembros por rol
    @Query("SELECT f.role, COUNT(f) FROM FamilyMember f GROUP BY f.role")
    List<Object[]> countMembersByRole();

    // Buscar miembros con gastos asociados
    @Query("SELECT DISTINCT f FROM FamilyMember f JOIN f.expenses e")
    List<FamilyMember> findMembersWithExpenses();
}
>>>>>>> 8ac3ca1b9896b9e44446dec7fdc59a6b7281c33c

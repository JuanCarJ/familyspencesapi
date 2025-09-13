package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.family.FamilyMemberDomain;
import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.Relationship;
import com.familyspencesapi.repositories.family.FamilyMemberRepository;
import com.familyspencesapi.repositories.users.DocumentTypeRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FamilyMemberService {

    @Autowired
    private FamilyMemberRepository repository;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    // === Expresiones regulares para validación ===
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^3\\d{9}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).{8,}$");

    // === Crear un familiar ===
    @Transactional
    public FamilyMemberDomain create(FamilyMemberDomain member) {
        validate(member);
        validateUniqueFields(member);

        // Verificar familia existente
        Family family = familyRepository.findById(member.getFamily().getId())
                .orElseThrow(() -> new IllegalArgumentException("Familia no encontrada"));
        member.setFamily(family);

        // Verificar tipo de documento
        DocumentType documentType = documentTypeRepository.findById(member.getDocumentType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado"));
        member.setDocumentType(documentType);

        // Verificar parentesco
        Relationship relationship = relationshipRepository.findById(member.getRelationship().getId())
                .orElseThrow(() -> new IllegalArgumentException("Parentesco no encontrado"));
        member.setRelationship(relationship);

        return repository.save(member);
    }

    // === Actualizar familiar ===
    @Transactional
    public FamilyMemberDomain update(UUID id, FamilyMemberDomain member) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Familiar no encontrado");
        }
        validate(member);
        member.setId(id);
        return repository.save(member);
    }

    // === Eliminar familiar ===
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Familiar no encontrado");
        }
        repository.deleteById(id);
    }

    // === Consultar todos ===
    public List<FamilyMemberDomain> getAll() {
        return repository.findAll();
    }

    // === Consultar por ID ===
    public FamilyMemberDomain getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Familiar no encontrado"));
    }

    // === Validaciones ===
    private void validate(FamilyMemberDomain member) {
        if (member == null) {
            throw new IllegalArgumentException("El familiar no puede ser nulo");
        }

        if (!StringUtils.hasText(member.getFullName()) || !NAME_PATTERN.matcher(member.getFullName()).matches()) {
            throw new IllegalArgumentException("Nombre completo inválido");
        }

        if (member.getBirthDate() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        LocalDate today = LocalDate.now();
        if (member.getBirthDate().isAfter(today) ||
                Period.between(member.getBirthDate(), today).getYears() > 150) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida");
        }

        if (!StringUtils.hasText(member.getDocumentNumber())) {
            throw new IllegalArgumentException("Número de documento obligatorio");
        }

        if (!StringUtils.hasText(member.getEmail()) || !EMAIL_PATTERN.matcher(member.getEmail()).matches()) {
            throw new IllegalArgumentException("Email inválido");
        }

        if (!StringUtils.hasText(member.getPhoneNumber()) || !PHONE_PATTERN.matcher(member.getPhoneNumber()).matches()) {
            throw new IllegalArgumentException("Teléfono inválido");
        }

        if (!StringUtils.hasText(member.getAddress()) || member.getAddress().length() < 5) {
            throw new IllegalArgumentException("Dirección inválida");
        }

        if (!StringUtils.hasText(member.getPassword()) ||
                !PASSWORD_PATTERN.matcher(member.getPassword()).matches()) {
            throw new IllegalArgumentException("Contraseña inválida");
        }

        if (!member.getPassword().equals(member.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
    }

    private void validateUniqueFields(FamilyMemberDomain member) {
        if (repository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Ya existe un familiar con este email");
        }
        if (repository.existsByDocumentNumber(member.getDocumentNumber())) {
            throw new IllegalArgumentException("Ya existe un familiar con este documento");
        }
    }
}

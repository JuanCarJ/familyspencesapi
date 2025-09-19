package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.domain.users.Relationship;
import com.familyspencesapi.repositories.users.DocumentTypeRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import com.familyspencesapi.repositories.users.RelationshipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FamilyMemberService {

    private final RegisterUserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final RelationshipRepository relationshipRepository;

    public FamilyMemberService(RelationshipRepository relationshipRepository,
                                           RegisterUserRepository userRepository,
                                           FamilyRepository familyRepository,
                                           DocumentTypeRepository documentTypeRepository) {
        this.relationshipRepository = relationshipRepository;
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.documentTypeRepository = documentTypeRepository;
    }

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^3\\d{9}$");
    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("^\\d{13,19}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).{8,}$");

    /**
     * Crea un nuevo usuario asociándolo a la familia del usuario logueado
     * @param user El nuevo usuario a registrar
     * @param familyId El ID de la familia del usuario logueado (como String desde el login)
     * @return El usuario registrado
     */
    @Transactional
    public RegisterUser createUser(RegisterUser user, String familyId) {
        validate(user);

        validateUniqueFields(user);

        // Convertir String a UUID y obtener la familia existente
        UUID familyUUID = convertStringToUUID(familyId);
        Family existingFamily = validateAndGetFamily(familyUUID);

        // Asociar el nuevo usuario a la familia existente
        user.setFamily(existingFamily);

        return userRepository.save(user);
    }

    /**
     * Sobrecarga del método para cuando se tiene el UUID de la familia
     * @param user El nuevo usuario a registrar
     * @param familyId El ID de la familia del usuario logueado como UUID
     * @return El usuario registrado
     */
    @Transactional
    public RegisterUser createUser(RegisterUser user, UUID familyId) {
        validate(user);

        validateUniqueFields(user);

        // Obtener la familia existente del usuario logueado
        Family existingFamily = validateAndGetFamily(familyId);

        // Asociar el nuevo usuario a la familia existente
        user.setFamily(existingFamily);

        return userRepository.save(user);
    }

    /**
     * Convierte un String a UUID y valida el formato
     * @param familyIdString ID de la familia como String
     * @return UUID válido
     */
    private UUID convertStringToUUID(String familyIdString) {
        if (!StringUtils.hasText(familyIdString)) {
            throw new IllegalArgumentException("El ID de la familia no puede estar vacío");
        }

        try {
            return UUID.fromString(familyIdString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Formato de ID de familia inválido: " + familyIdString);
        }
    }

    /**
     * Valida que la familia exista y la retorna
     * @param familyId ID de la familia
     * @return La familia encontrada
     */
    private Family validateAndGetFamily(UUID familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("El ID de la familia no puede ser nulo");
        }

        return familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));
    }

    /**
     * Valida que no existan campos únicos duplicados
     * @param user Usuario a validar
     */
    private void validateUniqueFields(RegisterUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }

        if (userRepository.existsByDocument(user.getdocument())) {
            throw new IllegalArgumentException("Ya existe un usuario con este documento");
        }
    }

    /**
     * Valida todos los campos del usuario
     * @param user Usuario a validar
     */
    public void validate(RegisterUser user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateBirthDate(user.getbirthDate());
        validateDocumentType(user.getdocumentType());
        validateDocument(user.getdocument());
        validateEmail(user.getEmail());
        validateRelationship(user.getRelationship());
        validateCreditCard(user.getcreditCard());
        validatePhone(user.getphone());
        validateAddress(user.getAddress());
        validatePassword(user.getPassword());
    }

    private void validateFirstName(String firstName) {
        if (!StringUtils.hasText(firstName) || !NAME_PATTERN.matcher(firstName).matches()) {
            throw new IllegalArgumentException(
                    "El nombre debe tener entre 2 y 50 letras y espacios, sin números ni símbolos"
            );
        }
    }

    private void validateLastName(String lastName) {
        if (!StringUtils.hasText(lastName) || !NAME_PATTERN.matcher(lastName).matches()) {
            throw new IllegalArgumentException(
                    "El apellido debe tener entre 2 y 50 letras y espacios, sin números ni símbolos"
            );
        }
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula");
        }
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today) ||
                Period.between(birthDate, today).getYears() > 150) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida o edad mayor a 150 años");
        }
    }

    private void validateDocumentType(DocumentType type) {
        if (type == null || type.getId() == null) {
            throw new IllegalArgumentException("Tipo de documento inválido");
        }
        documentTypeRepository.findById(type.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado"));
    }

    private void validateDocument(String document) {
        if (!StringUtils.hasText(document)) {
            throw new IllegalArgumentException("El documento no puede estar vacío");
        }
        if (!document.matches("\\d{6,15}")) {
            throw new IllegalArgumentException("El documento debe tener entre 6 y 15 dígitos");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido");
        }
    }

    private void validateRelationship(Relationship relationship) {
        if (relationship == null || relationship.getId() == null) {
            throw new IllegalArgumentException("Parentesco inválido");
        }
        relationshipRepository.findById(relationship.getId())
                .orElseThrow(() -> new IllegalArgumentException("Parentesco no encontrado"));
    }

    private void validateCreditCard(String creditCard) {
        if (!StringUtils.hasText(creditCard) || !CREDIT_CARD_PATTERN.matcher(creditCard).matches()) {
            throw new IllegalArgumentException("Número de tarjeta de crédito inválido (13-19 dígitos)");
        }
    }

    private void validatePhone(String phone) {
        if (!StringUtils.hasText(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Formato de teléfono inválido");
        }
    }

    private void validateAddress(String address) {
        if (!StringUtils.hasText(address) || address.length() < 5) {
            throw new IllegalArgumentException("La dirección no puede estar vacía y debe tener mínimo 5 caracteres");
        }
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password) ||
                !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener mínimo 8 caracteres, con minúscula, mayúscula, número y símbolo"
            );
        }
    }

    /**
     * Busca un usuario por ID
     * @param id ID del usuario
     * @return El usuario encontrado
     */
    public RegisterUser findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    /**
     * Verifica si existe un usuario con el email dado
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Verifica si existe un usuario con el documento dado
     * @param document Documento a verificar
     * @return true si existe, false si no
     */
    public boolean existsByDocument(String document) {
        return userRepository.existsByDocument(document);
    }

    /**
     * Obtiene todos los miembros de una familia
     * @param familyId ID de la familia
     * @return Lista de usuarios de la familia
     */
    public java.util.List<RegisterUser> getFamilyMembers(UUID familyId) {
        // Validar que la familia existe antes de buscar sus miembros
        validateAndGetFamily(familyId);

        // Obtener todos los miembros de la familia usando el método del repository
        return userRepository.findByFamily_Id(familyId);
    }

    /**
     * Sobrecarga que recibe familyId como String (desde login)
     * @param familyIdString ID de la familia como String
     * @return Lista de usuarios de la familia
     */
    public java.util.List<RegisterUser> getFamilyMembers(String familyIdString) {
        UUID familyId = convertStringToUUID(familyIdString);
        return getFamilyMembers(familyId);
    }
}
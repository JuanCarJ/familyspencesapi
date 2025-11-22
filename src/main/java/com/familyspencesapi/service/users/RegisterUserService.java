package com.familyspencesapi.service.users;

import com.familyspencesapi.config.messages.userprocessor.registeruser.UserRegisterProcessQueueConfig;
import com.familyspencesapi.domain.users.*;
import com.familyspencesapi.messages.users.MessageSenderBrokerUser;
import com.familyspencesapi.repositories.users.DocumentTypeRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import com.familyspencesapi.repositories.users.RelationshipRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class RegisterUserService {

    private final RegisterUserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final RelationshipRepository relationshipRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSenderBrokerUser messageSenderBrokerUser;
    private final UserRegisterProcessQueueConfig queueConfig;

    public RegisterUserService(RelationshipRepository relationshipRepository,
                               RegisterUserRepository userRepository,
                               FamilyRepository familyRepository,
                               DocumentTypeRepository documentTypeRepository,
                               PasswordEncoder passwordEncoder,
                               MessageSenderBrokerUser messageSenderBrokerUser,
                               UserRegisterProcessQueueConfig queueConfig) {
        this.relationshipRepository = relationshipRepository;
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSenderBrokerUser = messageSenderBrokerUser;
        this.queueConfig = queueConfig;
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

    // ===== MÉTODOS NUEVOS PARA EL EXPENSE CONTROLLER =====

    /**
     * Obtener todos los usuarios
     */
    @Transactional(readOnly = true)
    public List<RegisterUser> findAll() {
        return userRepository.findAll();
    }

    /**
     * Buscar usuario por ID - retorna Optional
     */
    @Transactional(readOnly = true)
    public Optional<RegisterUser> findById(UUID id) {
        return userRepository.findById(id);
    }

    /**
     * Obtener usuarios por familia
     */
    @Transactional(readOnly = true)
    public List<RegisterUser> findByFamilyId(UUID familyId) {
        return userRepository.findByFamily_Id(familyId);
    }

    // ===== MÉTODOS EXISTENTES =====

    @Transactional
    public RegisterUser createUser(RegisterUser user) {
        validate(user);
        validateUniqueFields(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setcreditCard(passwordEncoder.encode(user.getcreditCard()));
        Family family = createOrFindFamily(user);
        user.setFamily(family);

        RegisterUser savedUser = userRepository.save(user);

        RegisterUserMessage userData = new RegisterUserMessage(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getbirthDate(),
                savedUser.getdocumentType().getId(),
                savedUser.getdocument(),
                savedUser.getEmail(),
                savedUser.getRelationship().getId(),
                savedUser.getcreditCard(),
                savedUser.getphone(),
                savedUser.getAddress(),
                savedUser.getPassword(),
                savedUser.getFamily().getId()
        );

        messageSenderBrokerUser.execute(userData, queueConfig.getRoutingKeyUserCreate());

        return savedUser;
    }

    private Family createOrFindFamily(RegisterUser user) {
        String familyName = "Familia " + user.getLastName();
        Family newFamily = new Family(familyName);
        return familyRepository.save(newFamily);
    }

    private void validateUniqueFields(RegisterUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }

        if (userRepository.existsByDocument(user.getdocument())) {
            throw new IllegalArgumentException("Ya existe un usuario con este documento");
        }
    }

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

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByDocument(String document) {
        return userRepository.existsByDocument(document);
    }
}
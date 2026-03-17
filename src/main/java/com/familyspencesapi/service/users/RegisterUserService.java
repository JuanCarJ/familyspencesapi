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

    private static final String DELETED_FIRST_NAME = "cuenta";
    private static final String DELETED_LAST_NAME = "eliminada";

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
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]++(?:\\s[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]++)*+$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^3\\d{9}$");
    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("^\\d{13,19}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!#%*¿?&._-]).{8,}$");

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

    // ===== MÉTODOS EXISTENTES =====

    @Transactional
    public RegisterUser createUser(RegisterUser user) {
        validate(user);
        validateUniqueFields(user);

        // Validación de tipo de documento según edad
        if (user.getdocumentType().getType().equalsIgnoreCase("tarjeta de identidad") && isAdult(user.getbirthDate())) {
            throw new IllegalArgumentException("Una persona mayor de edad no puede tener Tarjeta de identidad");
        }

        if (user.getdocumentType().getType().equalsIgnoreCase("cédula de ciudadanía") && !isAdult(user.getbirthDate())) {
            throw new IllegalArgumentException("Una persona menor de edad no puede tener Cédula de ciudadanía");
        }

        // Validación de nombres prohibidos
        if (isNameForbidden(user.getFirstName().trim(), user.getLastName().trim())) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }

        if (user.getFirstName().trim().toLowerCase().contains("cuenta eliminada") ||
                user.getLastName().trim().toLowerCase().contains("cuenta eliminada")) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }

        if (user.getFirstName().trim().toLowerCase().contains(DELETED_FIRST_NAME) ||
                user.getFirstName().trim().toLowerCase().contains(DELETED_LAST_NAME) ||
                user.getLastName().trim().toLowerCase().contains(DELETED_FIRST_NAME) ||
                user.getLastName().trim().toLowerCase().contains(DELETED_LAST_NAME)) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String rawCard = user.getcreditCard();
        user.setCreditCardLast4(rawCard.substring(rawCard.length() - 4));
        user.setcreditCard(passwordEncoder.encode(rawCard));
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
                savedUser.getCreditCardLast4(),
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
            throw new IllegalArgumentException("El correo ya esta en uso");
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
        validateDocumentType(user);
        validateDocument(user.getdocument(), user.getdocumentType().getType());
        validateEmail(user.getEmail());
        validateRelationship(user);
        validateCreditCard(user.getcreditCard());
        validatePhone(user.getphone());
        validateAddress(user.getAddress());
        validatePassword(user.getPassword());
    }

    private void validateFirstName(String firstName) {
        if (!StringUtils.hasText(firstName) || !NAME_PATTERN.matcher(firstName).matches() ||
                firstName.length() < 3 || firstName.length() > 50) {
            throw new IllegalArgumentException(
                    "El nombre debe tener entre 3 y 50 letras y espacios, sin números ni símbolos"
            );
        }
    }

    private void validateLastName(String lastName) {
        if (!StringUtils.hasText(lastName) || !NAME_PATTERN.matcher(lastName).matches() ||
                lastName.length() < 3 || lastName.length() > 50) {
            throw new IllegalArgumentException(
                    "El apellido debe tener entre 3 y 50 letras y espacios, sin números ni símbolos"
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

    private void validateDocumentType(RegisterUser user) {
        DocumentType type = user.getdocumentType();
        if (type == null || type.getId() == null) {
            throw new IllegalArgumentException("Tipo de documento inválido");
        }
        DocumentType fullType = documentTypeRepository.findById(type.getId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no encontrado"));
        user.setdocumentType(fullType);
    }

    private void validateDocument(String document, String documentType) {
        if (!StringUtils.hasText(document)) {
            throw new IllegalArgumentException("El documento no puede estar vacío");
        }
        // Validación básica: 6-15 dígitos
        if (!document.matches("\\d{6,15}")) {
            throw new IllegalArgumentException("El documento debe tener entre 6 y 15 dígitos");
        }
        // Validación específica por tipo de documento
        validateDocumentByType(documentType, document);
    }

    private void validateDocumentByType(String documentType, String document) {
        switch (documentType.toLowerCase()) {
            case "cédula de ciudadanía", "cédula de extranjería":
                if (!document.matches("\\d{6,10}")) {
                    throw new IllegalArgumentException("La cédula debe tener entre 6 y 10 dígitos numéricos");
                }
                break;
            case "tarjeta de identidad", "registro civil":
                if (!document.matches("\\d{10,11}")) {
                    throw new IllegalArgumentException("El documento debe tener entre 10 y 11 dígitos numéricos");
                }
                break;
            case "pasaporte":
                if (!document.matches("[a-zA-Z0-9]{5,9}")) {
                    throw new IllegalArgumentException("El pasaporte debe tener entre 5 y 9 caracteres alfanuméricos");
                }
                break;
            case "número de identificación tributaria":
                if (!document.matches("\\d{9,10}")) {
                    throw new IllegalArgumentException("El NIT debe tener entre 9 y 10 dígitos numéricos");
                }
                break;
            case "permiso especial de permanencia":
                if (!document.matches("[a-zA-Z0-9]{4,16}")) {
                    throw new IllegalArgumentException("El PEP debe tener entre 4 y 16 caracteres alfanuméricos");
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo de documento no reconocido");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido");
        }
    }

    private void validateRelationship(RegisterUser user) {
        Relationship rel = user.getRelationship();
        if (rel == null || rel.getId() == null) {
            throw new IllegalArgumentException("Parentesco inválido");
        }
        Relationship fullRel = relationshipRepository.findById(rel.getId())
                .orElseThrow(() -> new IllegalArgumentException("Parentesco no encontrado"));
        user.setRelationship(fullRel);
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


    private boolean isNameForbidden(String firstName, String lastName) {
        return firstName.equalsIgnoreCase(DELETED_FIRST_NAME) &&
                lastName.equalsIgnoreCase(DELETED_LAST_NAME);
    }

    private boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears() >= 18;
    }
}
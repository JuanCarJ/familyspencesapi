package com.familyspencesapi.service.users;

import com.familyspencesapi.config.messages.userprocessor.registeruser.UserRegisterProcessQueueConfig;
import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.domain.users.UpdateUserMessage;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.ranking.RankingRepository;
import com.familyspencesapi.repositories.users.DocumentTypeRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class FamilyUserService {

    private static final String DELETED_FIRST_NAME = "cuenta";
    private static final String DELETED_LAST_NAME = "eliminada";

    private final RegisterUserRepository registerUserRepository;
    private final DocumentTypeRepository doucmentTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExpenseRepository expenseRepository;
    private final RankingRepository rankingRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserRegisterProcessQueueConfig queueConfig;

    public FamilyUserService(RegisterUserRepository registerUserRepository,
                             DocumentTypeRepository doucmentTypeRepository,
                             PasswordEncoder passwordEncoder,
                             ExpenseRepository expenseRepository,
                             RankingRepository rankingRepository,
                             RabbitTemplate rabbitTemplate,
                             UserRegisterProcessQueueConfig queueConfig) {
        this.registerUserRepository = registerUserRepository;
        this.doucmentTypeRepository = doucmentTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.expenseRepository = expenseRepository;
        this.rankingRepository = rankingRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.queueConfig = queueConfig;
    }

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,100}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^3\\d{9}$");
    private static final Pattern CREDIT_CARD_PATTERN =
            Pattern.compile("^\\d{13,19}$");


    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FamilyUserService.class);

    // GET user by email
    public RegisterUser getUserByEmail(String email) {
        return registerUserRepository.findByEmail(email).orElse(null);
    }

    // GET user by UUID
    public RegisterUser getUserById(UUID userId) {
        return registerUserRepository.findById(userId).orElse(null);
    }

    // POST change password
    public void changePassword(String email, String currentPassword, String newPassword) {
        RegisterUser user = registerUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        registerUserRepository.save(user);
    }


    // PATCH
    public RegisterUser updateUser(String email, RegisterUser updatedData) {
        Optional<RegisterUser> optionalUser = registerUserRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return null;
        }

        RegisterUser existingUser = optionalUser.get();

        if (updatedData.getFirstName() != null && !updatedData.getFirstName().isBlank()) {
            validateFirstName(updatedData.getFirstName());
            existingUser.setFirstName(updatedData.getFirstName());
        }
        if (updatedData.getLastName() != null && !updatedData.getLastName().isBlank()) {
            validateLastName(updatedData.getLastName());
            existingUser.setLastName(updatedData.getLastName());
        }
        if (updatedData.getbirthDate() != null) {
            existingUser.setbirthDate(updatedData.getbirthDate());
        }

        // Validate forbidden name "Cuenta Eliminada"
        String finalFirst = existingUser.getFirstName().trim().toLowerCase();
        String finalLast = existingUser.getLastName().trim().toLowerCase();
        if (finalFirst.contains(DELETED_FIRST_NAME) || finalFirst.contains(DELETED_LAST_NAME) ||
                finalLast.contains(DELETED_FIRST_NAME) || finalLast.contains(DELETED_LAST_NAME)) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }

        log.info("PATCH documentType recibido: {}, id: {}",
                updatedData.getdocumentType(),
                updatedData.getdocumentType() != null ? updatedData.getdocumentType().getId() : "null");
        if (updatedData.getdocumentType() != null && updatedData.getdocumentType().getId() != null) {
            validateDocumentType(updatedData.getdocumentType());
            existingUser.setdocumentType(updatedData.getdocumentType());
        }
        if (updatedData.getdocument() != null && !updatedData.getdocument().isBlank()) {
            validateDocument(updatedData.getdocument());
            existingUser.setdocument(updatedData.getdocument());
        }
        if (updatedData.getcreditCard() != null && !updatedData.getcreditCard().isBlank()) {
            validateCreditCard(updatedData.getcreditCard());
            String rawCard = updatedData.getcreditCard();
            existingUser.setCreditCardLast4(rawCard.substring(rawCard.length() - 4));
            existingUser.setcreditCard(passwordEncoder.encode(rawCard));
        }
        if (updatedData.getphone() != null && !updatedData.getphone().isBlank()) {
            validatePhone(updatedData.getphone());
            existingUser.setphone(updatedData.getphone());
        }
        if (updatedData.getAddress() != null && !updatedData.getAddress().isBlank()) {
            validateAddress(updatedData.getAddress());
            existingUser.setAddress(updatedData.getAddress());
        }

        return registerUserRepository.save(existingUser);
    }

    // PUT (actualización completa)
    public RegisterUser updateAllUser(String email, RegisterUser updatedUser) {
        log.info("PUT updateAllUser - documentType: {}, id: {}",
                updatedUser.getdocumentType(),
                updatedUser.getdocumentType() != null ? updatedUser.getdocumentType().getId() : "null");
        Optional<RegisterUser> optionalUser = registerUserRepository.findByEmail(email);
        validate(updatedUser);
        validateForbiddenName(updatedUser.getFirstName(), updatedUser.getLastName());
        if (optionalUser.isEmpty()) {
            return null;
        }

        RegisterUser existingUser = optionalUser.get();
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        if (updatedUser.getbirthDate() != null) {
            existingUser.setbirthDate(updatedUser.getbirthDate());
        }
        existingUser.setdocumentType(updatedUser.getdocumentType());
        existingUser.setdocument(updatedUser.getdocument());
        String rawCard = updatedUser.getcreditCard();
        existingUser.setCreditCardLast4(rawCard.substring(rawCard.length() - 4));
        existingUser.setcreditCard(passwordEncoder.encode(rawCard));
        existingUser.setphone(updatedUser.getphone());
        existingUser.setAddress(updatedUser.getAddress());

        return registerUserRepository.save(existingUser);
    }

    public void validate(RegisterUser user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
        validateDocumentType(user.getdocumentType());
        validateDocument(user.getdocument());
        validateCreditCard(user.getcreditCard());
        validatePhone(user.getphone());
        validateAddress(user.getAddress());
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

    private void validateDocumentType(DocumentType type) {
        if (type == null || type.getId() == null || doucmentTypeRepository.findById(type.getId()).isEmpty()) {
            throw new IllegalArgumentException("Tipo de documento invalido");
        }
    }
    private void validateDocument(String document) {
        if (!StringUtils.hasText(document)) {
            throw new IllegalArgumentException("El documento no puede estar vacio");
        }
        if (!document.matches("\\d{6,15}")) {
            throw new IllegalArgumentException("El documento debe tener entre 6 y 15 digitos");
        }
    }

    private void validateCreditCard(String creditCard) {
        if (!StringUtils.hasText(creditCard) || !CREDIT_CARD_PATTERN.matcher(creditCard).matches()) {
            throw new IllegalArgumentException("Numero de tarjeta de credito invalido (13-19 digitos)");
        }
    }

    private void validatePhone(String phone) {
        if (!StringUtils.hasText(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Formato de telefono invalido");
        }
    }

    private void validateAddress(String address) {
        if (!StringUtils.hasText(address) || address.length() < 5) {
            throw new IllegalArgumentException("La direccion no puede estar vacia y debe tener minimo 5 caracteres");
        }
    }

    private void validateForbiddenName(String firstName, String lastName) {
        String first = firstName.trim().toLowerCase();
        String last = lastName.trim().toLowerCase();
        if (first.contains(DELETED_FIRST_NAME) || first.contains(DELETED_LAST_NAME) ||
                last.contains(DELETED_FIRST_NAME) || last.contains(DELETED_LAST_NAME)) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }
    }

    // PUT by UUID: publish update message to RabbitMQ
    public void updateUserById(UUID id, RegisterUser updatedUser) {
        RegisterUser existing = registerUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        UpdateUserMessage msg = new UpdateUserMessage(
                existing.getId(),
                updatedUser.getFirstName() != null ? updatedUser.getFirstName() : existing.getFirstName(),
                updatedUser.getLastName() != null ? updatedUser.getLastName() : existing.getLastName(),
                updatedUser.getbirthDate() != null ? updatedUser.getbirthDate() : existing.getbirthDate(),
                updatedUser.getdocumentType() != null ? updatedUser.getdocumentType().getId() : existing.getdocumentType().getId(),
                updatedUser.getdocument() != null ? updatedUser.getdocument() : existing.getdocument(),
                updatedUser.getRelationship() != null ? updatedUser.getRelationship().getId() : existing.getRelationship().getId(),
                updatedUser.getcreditCard(),
                updatedUser.getphone() != null ? updatedUser.getphone() : existing.getphone(),
                updatedUser.getAddress() != null ? updatedUser.getAddress() : existing.getAddress()
        );

        rabbitTemplate.convertAndSend(queueConfig.getExchangeName(), queueConfig.getRoutingKeyUserUpdate(), msg);
    }

    // DELETE by UUID: deactivate immediately and publish to queue
    @Transactional
    public void deactivateUserById(UUID id) {
        RegisterUser user = registerUserRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        user.setActive(false);
        registerUserRepository.save(user);

        rabbitTemplate.convertAndSend(queueConfig.getExchangeName(), queueConfig.getRoutingKeyUserDeactivate(), id.toString());
    }

    // Soft delete: mark user as "Cuenta Eliminada", update associated data, and deactivate
    @Transactional
    public void deleteAccount(String email) {
        RegisterUser user = registerUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        // Update expenses: responsible field stores the user's email as plain text
        expenseRepository.updateResponsibleByEmail(user.getEmail(), "Cuenta Eliminada");

        // Update rankings: fullName field stores the user's name as plain text
        rankingRepository.updateFullNameByUserId(user.getId(), "Cuenta Eliminada");

        // Mark user as "Cuenta Eliminada" and deactivate
        user.setFirstName("Cuenta");
        user.setLastName("Eliminada");
        user.setActive(false);
        registerUserRepository.save(user);
    }
}


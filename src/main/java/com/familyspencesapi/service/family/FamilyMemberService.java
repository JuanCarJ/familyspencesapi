package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.domain.users.RegisterUserMessage;
import com.familyspencesapi.messages.familymember.MessageSenderBrokerFamilyMember;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
public class FamilyMemberService {

    private static final Logger logger = Logger.getLogger(FamilyMemberService.class.getName());
    private final RegisterUserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final MessageSenderBrokerFamilyMember messageSender;

    private static final String DELETED_FIRST_NAME = "cuenta";
    private static final String DELETED_LAST_NAME  = "eliminada";

    public FamilyMemberService(RegisterUserRepository userRepository, FamilyRepository familyRepository, MessageSenderBrokerFamilyMember messageSender) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.messageSender = messageSender;
    }


    @Transactional
    public String createUser(RegisterUser user, String familyId) {

        UUID familyIdAsUUID = UUID.fromString(familyId);

        Family family = familyRepository.findById(familyIdAsUUID)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con este email");
        }

        if (userRepository.existsByDocument(user.getdocument())) {
            throw new IllegalArgumentException("Ya existe un usuario con este documento");
        }

        if (isNameForbidden(user.getFirstName().trim(), user.getLastName().trim())) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }

        if (user.getFirstName().trim().toLowerCase().contains("cuenta eliminada") ||
                user.getLastName().trim().toLowerCase().contains("cuenta eliminada")) {

            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }
        if (user.getFirstName().trim().toLowerCase().contains("cuenta") ||
                user.getFirstName().trim().toLowerCase().contains("eliminada") ||
                user.getLastName().trim().toLowerCase().contains("cuenta") ||
                user.getLastName().trim().toLowerCase().contains("eliminada")) {
            throw new IllegalArgumentException("El nombre del usuario no es válido");
        }

        user.setFamily(family);

        try {
            RegisterUserMessage message = new RegisterUserMessage(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getbirthDate(),
                    user.getdocumentType().getId(),
                    user.getdocument(),
                    user.getEmail(),
                    user.getRelationship().getId(),
                    user.getcreditCard(),
                    user.getphone(),
                    user.getAddress(),
                    user.getPassword(),
                    user.getFamily().getId()
            );

            messageSender.execute(message, "user.add.member");

            logger.info("Mensaje enviado a RabbitMQ para usuario: " + user.getEmail());
            return "El mensaje se envio de forma exitosa";

        } catch (Exception e) {
            logger.severe("Error enviando mensaje a RabbitMQ: " + e.getMessage());
            throw new RuntimeException("Error sending message to RabbitMQ", e);
        }
    }


    private void validateAndGetFamily(UUID familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("El ID de la familia no puede ser nulo");
        }

        familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));
    }

    private boolean isNameForbidden(String firstName, String lastName) {
        return firstName.equalsIgnoreCase(DELETED_FIRST_NAME) &&
                lastName.equalsIgnoreCase(DELETED_LAST_NAME);
    }

    public List<RegisterUser> getFamilyMembers(UUID familyId) {
        validateAndGetFamily(familyId);
        return userRepository.findByFamily_Id(familyId)
                .stream()
                .filter(u -> !(
                        u.getFirstName().trim().toLowerCase().contains("cuenta") ||
                                u.getFirstName().trim().toLowerCase().contains("eliminada") ||
                                u.getLastName().trim().toLowerCase().contains("cuenta") ||
                                u.getLastName().trim().toLowerCase().contains("eliminada")
                ))
                .collect(Collectors.toList());
    }

}
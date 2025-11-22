package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.FamilyMember;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.domain.users.RegisterUserMessage;
import com.familyspencesapi.messages.familymember.MessageSenderBrokerFamilyMember;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;
import java.util.logging.Logger;


@Service
public class FamilyMemberService {

    private static final Logger logger = Logger.getLogger(FamilyMemberService.class.getName());
    private final RegisterUserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final MessageSenderBrokerFamilyMember messageSender;

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

    public java.util.List<RegisterUser> getFamilyMembers(UUID familyId) {
        validateAndGetFamily(familyId);
        return userRepository.findByFamily_Id(familyId);
    }
    private void validateAndGetFamily(UUID familyId) {
        if (familyId == null) {
            throw new IllegalArgumentException("El ID de la familia no puede ser nulo");
        }

        familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));
    }

}
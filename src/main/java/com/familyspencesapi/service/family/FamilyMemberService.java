package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.messages.familymember.MessageSenderBrokerFamilyMember;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
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
    public RegisterUser createUser(RegisterUser user, String familyId) {

        UUID familyIdAsUUID=UUID.fromString(familyId);

        Family family = familyRepository.findById(familyIdAsUUID)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));


        user.setFamily(family);

        RegisterUser savedUser = userRepository.save(user);

        try {
            messageSender.execute(savedUser, "family.member.created");
            logger.info("Mensaje enviado a RabbitMQ: " + savedUser.getEmail());
        } catch (Exception e) {
            logger.info("Error enviando mensaje a RabbitMQ: " + e.getMessage());
        }

        return savedUser;
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

    public List<Family> getFamlies(){
        return familyRepository.findAll();
    }

}
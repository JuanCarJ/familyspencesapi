package com.familyspencesapi.service.family;

import com.familyspencesapi.domain.users.DocumentType;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.domain.users.RegisterUserMessage;
import com.familyspencesapi.messages.familymember.MessageSenderBrokerFamilyMember;
import com.familyspencesapi.repositories.users.DocumentTypeRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private final DocumentTypeRepository documentTypeRepository;

    private static final String DELETED_FIRST_NAME = "cuenta";
    private static final String DELETED_LAST_NAME  = "eliminada";

    public FamilyMemberService(RegisterUserRepository userRepository, FamilyRepository familyRepository, MessageSenderBrokerFamilyMember messageSender, DocumentTypeRepository documentTypeRepository) {
        this.userRepository = userRepository;
        this.familyRepository = familyRepository;
        this.messageSender = messageSender;
        this.documentTypeRepository = documentTypeRepository;
    }

    @Transactional
    public String createUser(RegisterUser user, String familyId) {

        UUID familyIdAsUUID = UUID.fromString(familyId);

        Family family = familyRepository.findById(familyIdAsUUID)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la familia con ID: " + familyId));

        if (userRepository.existsByEmailAndFamily_Id(user.getEmail(),familyIdAsUUID)) {
            throw new IllegalArgumentException("Ya existe un usuario con este email en este grupo familiar");
        }

        if (userRepository.existsByDocumentAndFamily_Id(user.getdocument(),familyIdAsUUID)) {
            throw new IllegalArgumentException("Ya existe un usuario con este documento en este grupo familiar");
        }

        if(user.getbirthDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("La fecha de nacimiento no es válida");
        }

        DocumentType documentType=documentTypeRepository.findById(user.getdocumentType().getId()).
                orElseThrow(()->new IllegalArgumentException("No se encontro el tipo de documento"));
        user.setdocumentType(documentType);

        if (user.getdocumentType().getType().equals("Tarjeta de identidad") && isAdult(user.getbirthDate())) {
            throw new IllegalArgumentException("Una persona mayor de edad no puede tener Tarjeta de identidad");
        }


        if (user.getdocumentType().getType().equals("Cédula de ciudadanía") && !isAdult(user.getbirthDate())) {
            throw new IllegalArgumentException("Una persona menor de edad no puede tener Cédula de ciudadanía");
        }

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

        validateDocument(user.getdocumentType().getType(),user.getdocument());

        user.setFamily(family);

        user.setFirstName(user.getFirstName().trim());
        user.setLastName(user.getLastName().trim());
        user.setAddress(user.getAddress().trim());
        user.setEmail(user.getEmail().trim());
        user.setdocument(user.getdocument().trim());

        validateUserFields(user);

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
                        u.getFirstName().trim().toLowerCase().contains(DELETED_FIRST_NAME) ||
                                u.getFirstName().trim().toLowerCase().contains(DELETED_LAST_NAME) ||
                                u.getLastName().trim().toLowerCase().contains(DELETED_FIRST_NAME) ||
                                u.getLastName().trim().toLowerCase().contains(DELETED_LAST_NAME)
                ))
                .collect(Collectors.toList());
    }

    private boolean isAdult(LocalDate birthDate){
        long years = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        return years >= 18;

    }

    private void validateDocument(String documentType, String document) {
        switch (documentType.toLowerCase()) {
            case "cédula de ciudadanía":
            case "cédula de extranjería":
                if (!document.matches("\\d{6,10}"))
                    throw new IllegalArgumentException("La cédula debe tener entre 6 y 10 dígitos numéricos");
                break;
            case "tarjeta de identidad":
            case "registro civil":
                if (!document.matches("\\d{10,11}"))
                    throw new IllegalArgumentException("El documento debe tener entre 10 y 11 dígitos numéricos");
                break;
            case "pasaporte":
                if (!document.matches("[a-zA-Z0-9]{5,9}"))
                    throw new IllegalArgumentException("El pasaporte debe tener entre 5 y 9 caracteres alfanuméricos");
                break;
            case "número de identificación tributaria":
                if (!document.matches("\\d{9,10}"))
                    throw new IllegalArgumentException("El NIT debe tener entre 9 y 10 dígitos numéricos");
                break;
            case "permiso especial de permanencia":
                if (!document.matches("[a-zA-Z0-9]{4,16}"))
                    throw new IllegalArgumentException("El PEP debe tener entre 4 y 16 caracteres alfanuméricos");
                break;
            default:
                throw new IllegalArgumentException("Tipo de documento no reconocido");
        }
    }

    private void validateUserFields(RegisterUser user) {

        String nameRegex = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+(\\s[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ]+)*$";
        if (!user.getFirstName().matches(nameRegex) || user.getFirstName().length() < 3 || user.getFirstName().length() > 50)
            throw new IllegalArgumentException("El nombre no es válido");
        if (!user.getLastName().matches(nameRegex) || user.getLastName().length() < 3 || user.getLastName().length() > 50)
            throw new IllegalArgumentException("El apellido no es válido");

        if (!user.getEmail().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("El email no es válido");

        if (!user.getphone().matches("^3\\d{9}$"))
            throw new IllegalArgumentException("El teléfono debe empezar con 3 y tener 10 dígitos");

        if (!user.getcreditCard().matches("^\\d{13,19}$"))
            throw new IllegalArgumentException("La tarjeta de crédito debe tener entre 13 y 19 dígitos");

        if (user.getAddress().trim().length() < 5)
            throw new IllegalArgumentException("La dirección debe tener mínimo 5 caracteres");

        if (!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!#%*¿?&._-]).{8,}$"))
            throw new IllegalArgumentException("La contraseña no cumple los requisitos de seguridad");
    }

}
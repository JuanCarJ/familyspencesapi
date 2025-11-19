package com.familyspencesapi.service.vacation;

import com.familyspencesapi.domain.vacation.Vacation;
import com.familyspencesapi.messages.vacation.VacationMessageSender;
import com.familyspencesapi.repositories.vacation.VacationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class VacationService {

    private static final Logger logger = LoggerFactory.getLogger(VacationService.class);

    private final VacationRepository repository;
    private final VacationMessageSender messageSender;

    public VacationService(VacationRepository repository,
                           VacationMessageSender messageSender) {
        this.repository = repository;
        this.messageSender = messageSender;
    }

    // Métodos de consulta (sin RabbitMQ)
    public List<Vacation> getAllVacations() {
        return repository.findAll();
    }

    public Optional<Vacation> getVacationById(UUID id) {
        return repository.findById(id);
    }

    // Crear vacación con evento RabbitMQ
    @Transactional
    public Vacation createVacation(Vacation vacation) {
        try {
            // Guardar en base de datos
            Vacation savedVacation = repository.save(vacation);
            logger.info("Vacación creada en BD: {}", savedVacation.getId());

            // Enviar mensaje a RabbitMQ
            messageSender.sendVacationCreated(savedVacation);
            logger.info("Mensaje de creación enviado a RabbitMQ: {}", savedVacation.getId());

            return savedVacation;
        } catch (Exception e) {
            logger.error("Error creando vacación", e);
            throw new RuntimeException("Error al crear vacación", e);
        }
    }

    // Actualizar vacación con evento RabbitMQ
    @Transactional
    public Optional<Vacation> updateVacation(UUID id, Vacation vacationDetails) {
        return repository.findById(id).map(vacation -> {
            try {
                // Actualizar campos
                vacation.setTitulo(vacationDetails.getTitulo());
                vacation.setDescripcion(vacationDetails.getDescripcion());
                vacation.setFechaInicio(vacationDetails.getFechaInicio());
                vacation.setFechaFin(vacationDetails.getFechaFin());
                vacation.setLugar(vacationDetails.getLugar());
                vacation.setPresupuesto(vacationDetails.getPresupuesto());

                Vacation updatedVacation = repository.save(vacation);
                logger.info("Vacación actualizada en BD: {}", updatedVacation.getId());

                // Enviar mensaje a RabbitMQ
                messageSender.sendVacationUpdated(updatedVacation);
                logger.info("Mensaje de actualización enviado a RabbitMQ: {}", updatedVacation.getId());

                return updatedVacation;
            } catch (Exception e) {
                logger.error("Error actualizando vacación: {}", id, e);
                throw new RuntimeException("Error al actualizar vacación", e);
            }
        });
    }

    // Eliminar vacación con evento RabbitMQ
    @Transactional
    public boolean deleteVacation(UUID id) {
        return repository.findById(id).map(vacation -> {
            try {
                // Eliminar de base de datos
                repository.delete(vacation);
                logger.info("Vacación eliminada de BD: {}", id);

                // Preparar datos para el mensaje
                Map<String, String> data = new HashMap<>();
                data.put("id", id.toString());
                data.put("titulo", vacation.getTitulo());
                data.put("lugar", vacation.getLugar());
                data.put("deletedAt", java.time.LocalDateTime.now().toString());

                // Enviar mensaje a RabbitMQ
                messageSender.sendVacationDeleted(data);
                logger.info("Mensaje de eliminación enviado a RabbitMQ: {}", id);

                return true;
            } catch (Exception e) {
                logger.error("Error eliminando vacación: {}", id, e);
                throw new RuntimeException("Error al eliminar vacación", e);
            }
        }).orElse(false);
    }
}
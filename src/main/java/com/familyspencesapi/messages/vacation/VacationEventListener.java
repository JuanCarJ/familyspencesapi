package com.familyspencesapi.messages.vacation;

import com.familyspencesapi.config.messages.vacation.VacationQueueConfig;
import com.familyspencesapi.domain.vacation.Vacation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VacationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(VacationEventListener.class);

    private final VacationQueueConfig queueConfig;

    public VacationEventListener(VacationQueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    @RabbitListener(queues = "${vacation.queue.name:vacation-events}")
    public void handleVacationEvent(String messageJson) {
        logger.info("Mensaje recibido en cola de vacaciones: {}", messageJson);

        try {
            // Aquí procesarías el JSON según el routing key
            // Podrías deserializar a Vacation o Map según el tipo de mensaje
            processMessage(messageJson);
        } catch (Exception e) {
            logger.error("Error procesando mensaje de vacación", e);
            // NO re-lanzar para evitar reintentos infinitos
        }
    }

    // Listeners específicos por routing key (alternativa más estructurada)
    @RabbitListener(queues = "${vacation.queue.name:vacation-events}")
    public void handleVacationCreated(Vacation vacation) {
        logger.info("Vacación CREADA: {} - {} (ID: {})",
                vacation.getTitulo(),
                vacation.getLugar(),
                vacation.getId());

        try {
            // Lógica de negocio - NO GUARDAR DE NUEVO
            sendNotification(vacation, "Nueva vacación creada");
            validateBudget(vacation);

            logger.info("Vacación creada procesada exitosamente: {}", vacation.getId());
        } catch (Exception e) {
            logger.error("Error procesando creación de vacación: {}", vacation.getId(), e);
            // NO re-lanzar para evitar reintentos
        }
    }

    @RabbitListener(queues = "${vacation.queue.name:vacation-events}")
    public void handleVacationUpdated(Vacation vacation) {
        logger.info("Vacación ACTUALIZADA: {} (ID: {})",
                vacation.getTitulo(),
                vacation.getId());

        try {
            // Lógica de negocio - NO GUARDAR DE NUEVO
            sendNotification(vacation, "Vacación actualizada");
            validateBudget(vacation);

            logger.info("Vacación actualizada procesada exitosamente: {}", vacation.getId());
        } catch (Exception e) {
            logger.error("Error procesando actualización de vacación: {}", vacation.getId(), e);
            // NO re-lanzar para evitar reintentos
        }
    }

    @RabbitListener(queues = "${vacation.queue.name:vacation-events}")
    public void handleVacationDeleted(Map<String, String> data) {
        String vacationId = data.get("id");
        logger.info("Vacación ELIMINADA: ID {}", vacationId);

        try {
            // Lógica de limpieza - NO ELIMINAR DE NUEVO
            sendDeletionNotification(data);
            archiveVacationData(vacationId);

            logger.info("Vacación eliminada procesada exitosamente: {}", vacationId);
        } catch (Exception e) {
            logger.error("Error procesando eliminación de vacación: {}", vacationId, e);
            // NO re-lanzar para evitar reintentos
        }
    }

    private void processMessage(String messageJson) {
        // Lógica genérica de procesamiento
        logger.debug("Procesando mensaje: {}", messageJson);
    }

    private void sendNotification(Vacation vacation, String message) {
        logger.info("Notificación: {} - Vacación: {} ({})",
                message, vacation.getTitulo(), vacation.getLugar());

        // TODO: Integrar con servicio de notificaciones
        // - Email
        // - SMS
        // - Push notifications
    }

    private void sendDeletionNotification(Map<String, String> data) {
        logger.info("Notificación de eliminación para ID: {}", data.get("id"));
        // TODO: Implementar notificación de eliminación
    }

    private void validateBudget(Vacation vacation) {
        if (vacation.getPresupuesto() != null &&
                vacation.getPresupuesto().doubleValue() > 10000) {
            logger.warn("⚠️ Presupuesto alto detectado: {} para vacación: {}",
                    vacation.getPresupuesto(), vacation.getTitulo());
            // TODO: Enviar alerta o requerir aprobación
        }
    }

    private void archiveVacationData(String vacationId) {
        logger.info("Archivando datos de vacación: {}", vacationId);
        // TODO: Implementar lógica de archivo
    }
}
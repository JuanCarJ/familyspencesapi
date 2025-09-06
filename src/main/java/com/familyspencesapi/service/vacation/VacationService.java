package com.familyspencesapi.service.vacation;

import com.familyspencesapi.domain.vacation.Vacation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VacationService {

    private final Map<Long, Vacation> vacations = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(6);

    public VacationService() {
        initializeBurnedData();
    }

    private void initializeBurnedData() {
        Vacation cartagena = new Vacation(
                1L,
                "Escapada Caribeña a Cartagena",
                "Vacaciones familiares en la ciudad amurallada. Playas, historia y gastronomía caribeña. Incluye tours por el centro histórico y tiempo en Playa Blanca.",
                LocalDate.of(2024, 12, 20),
                LocalDate.of(2024, 12, 27),
                "Cartagena, Colombia",
                new BigDecimal("2500000.00")
        );
        vacations.put(1L, cartagena);

        Vacation ejeCafetero = new Vacation(
                2L,
                "Aventura en el Eje Cafetero",
                "Tour por fincas cafeteras, paisajes montañosos y pueblos pintorescos como Salento. Incluye caminata por el Valle de Cocora y cata de café.",
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 1, 20),
                "Eje Cafetero, Colombia",
                new BigDecimal("1800000.00")
        );
        vacations.put(2L, ejeCafetero);

        Vacation sanAndres = new Vacation(
                3L,
                "Paraíso en San Andrés",
                "Paraíso caribeño con mar de siete colores. Buceo, snorkeling y playas paradisíacas. Tour por Johnny Cay y acuario natural.",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 17),
                "San Andrés, Colombia",
                new BigDecimal("3200000.00")
        );
        vacations.put(3L, sanAndres);

        Vacation medellin = new Vacation(
                4L,
                "Medellín Ciudad Innovadora",
                "Ciudad de la eterna primavera. Museos, parques, teleféricos y vida nocturna. Tour por Comuna 13 y Parque Arví.",
                LocalDate.of(2025, 4, 5),
                LocalDate.of(2025, 4, 10),
                "Medellín, Colombia",
                new BigDecimal("1500000.00")
        );
        vacations.put(4L, medellin);

        Vacation tayrona = new Vacation(
                5L,
                "Naturaleza en el Tayrona",
                "Naturaleza exuberante, playas vírgenes y senderismo. Conexión total con la naturaleza. Camping en Cabo San Juan y avistamiento de aves.",
                LocalDate.of(2025, 6, 20),
                LocalDate.of(2025, 6, 25),
                "Parque Tayrona, Colombia",
                new BigDecimal("2000000.00")
        );
        vacations.put(5L, tayrona);
    }

    public Vacation createVacation(Vacation vacation) {
        if (vacation == null) {
            throw new IllegalArgumentException("La vacación no puede ser null");
        }

        Long newId = idGenerator.getAndIncrement();
        vacation.setId(newId);

        vacations.put(newId, vacation);

        return vacation;
    }

    public Optional<Vacation> updateVacation(Long id, Vacation vacacionActualizada) {
        if (id == null || vacacionActualizada == null) {
            throw new IllegalArgumentException("El ID y la vacación no pueden ser null");
        }

        if (vacations.containsKey(id)) {
            vacacionActualizada.setId(id);
            vacations.put(id, vacacionActualizada);

            return Optional.of(vacacionActualizada);
        }

        return Optional.empty();
    }

    public boolean deteleVacation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser null");
        }

        return vacations.remove(id) != null;
    }

    public Optional<Vacation> getVacationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser null");
        }

        return Optional.ofNullable(vacations.get(id));
    }

    public List<Vacation> getAllVacations() {
        return new ArrayList<>(vacations.values());
    }

    public int countVacation() {
        return vacations.size();
    }

    public boolean existsVacation(Long id) {
        if (id == null) {
            return false;
        }
        return vacations.containsKey(id);
    }

    public void deleteAllVacations() {
        vacations.clear();
        idGenerator.set(1);
    }

    public List<Vacation> searchByPlace(String lugar) {
        if (lugar == null || lugar.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return vacations.values().stream()
                .filter(vacation -> vacation.getLugar() != null &&
                        vacation.getLugar().toLowerCase()
                                .contains(lugar.toLowerCase()))
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }

    public List<Vacation> searchByTitle(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return vacations.values().stream()
                .filter(vacation -> vacation.getTitulo() != null &&
                        vacation.getTitulo().toLowerCase()
                                .contains(titulo.toLowerCase()))
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }

    public List<Vacation> SearchByBudgetRange(BigDecimal presupuestoMin, BigDecimal presupuestoMax) {
        if (presupuestoMin == null || presupuestoMax == null) {
            throw new IllegalArgumentException("Los presupuestos no pueden ser null");
        }

        return vacations.values().stream()
                .filter(vacation -> vacation.getPresupuesto() != null &&
                        vacation.getPresupuesto().compareTo(presupuestoMin) >= 0 &&
                        vacation.getPresupuesto().compareTo(presupuestoMax) <= 0)
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }

    public List<Vacation> SearchByDateRange(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser null");
        }

        return vacations.values().stream()
                .filter(vacation -> vacation.getFechaInicio() != null && vacation.getFechaFin() != null)
                .filter(vacation ->
                        !vacation.getFechaFin().isBefore(fechaInicio) &&
                                !vacation.getFechaInicio().isAfter(fechaFin))
                .collect(ArrayList::new, (list, item) -> list.add(item), (list1, list2) -> list1.addAll(list2));
    }
}

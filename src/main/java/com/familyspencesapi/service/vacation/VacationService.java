package com.familyspencesapi.service.vacation;

import com.familyspencesapi.domain.vacation.Vacation;
import com.familyspencesapi.repositories.vacation.VacationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VacationService {

    private final VacationRepository repository;

    public VacationService(VacationRepository repository) {
        this.repository = repository;
    }

    public List<Vacation> getAllVacations() {
        return repository.findAll();
    }

    public Optional<Vacation> getVacationById(UUID id) {
        return repository.findById(id);
    }

    public Vacation createVacation(Vacation vacation) {
        return repository.save(vacation);
    }

    public Optional<Vacation> updateVacation(UUID id, Vacation vacationDetails) {
        return repository.findById(id).map(vacation -> {
            vacation.setTitulo(vacationDetails.getTitulo());
            vacation.setDescripcion(vacationDetails.getDescripcion());
            vacation.setFechaInicio(vacationDetails.getFechaInicio());
            vacation.setFechaFin(vacationDetails.getFechaFin());
            vacation.setLugar(vacationDetails.getLugar());
            vacation.setPresupuesto(vacationDetails.getPresupuesto());
            return repository.save(vacation);
        });
    }

    public boolean deleteVacation(UUID id) {
        return repository.findById(id).map(vacation -> {
            repository.delete(vacation);
            return true;
        }).orElse(false);
    }
}

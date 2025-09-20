package com.familyspencesapi.controllers.vacation;

import com.familyspencesapi.domain.vacation.Vacation;
import com.familyspencesapi.service.vacation.VacationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacations")
public class VacationController {

    private final VacationService vacationService;

    public VacationController(VacationService vacationService) {
        this.vacationService = vacationService;
    }

    @GetMapping
    public ResponseEntity<List<Vacation>> getAllVacations() {
        return ResponseEntity.ok(vacationService.getAllVacations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vacation> getVacationById(@PathVariable UUID id) {
        Optional<Vacation> vacation = vacationService.getVacationById(id);
        return vacation.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Vacation> createVacation(@RequestBody Vacation vacation) {
        Vacation savedVacation = vacationService.createVacation(vacation);
        return ResponseEntity
                .created(URI.create("/api/vacations/" + savedVacation.getId()))
                .body(savedVacation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vacation> updateVacation(@PathVariable UUID id, @RequestBody Vacation vacationDetails) {
        return vacationService.updateVacation(id, vacationDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacation(@PathVariable UUID id) {
        boolean deleted = vacationService.deleteVacation(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

package com.familyspencesapi.controller;

import com.familyspencesapi.domain.vacations.Vacations;
import com.familyspencesapi.service.VacationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vacations")
@CrossOrigin(origins = "*")
public class VacationController {

    @Autowired
    private VacationService vacationService;

    @GetMapping
    public ResponseEntity<List<Vacations>> getAllVacations() {
        try {
            List<Vacations> vacaciones = vacationService.getAllVacations();
            return ResponseEntity.ok(vacaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vacations> getVacationById(@PathVariable Long id) {
        try {
            Optional<Vacations> vacacion = vacationService.getVacationById(id);
            if (vacacion.isPresent()) {
                return ResponseEntity.ok(vacacion.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/lugar/{lugar}")
    public ResponseEntity<List<Vacations>> getVacationsByLocation(@PathVariable String lugar) {
        try {
            List<Vacations> vacaciones = vacationService.getVacationsByLocation(lugar);
            return ResponseEntity.ok(vacaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/presupuesto")
    public ResponseEntity<List<Vacations>> getVacationsByBudget(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        try {
            List<Vacations> vacaciones = vacationService.getVacationsByBudget(min, max);
            return ResponseEntity.ok(vacaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping
    public ResponseEntity<Vacations> createVacation(@RequestBody Vacations vacation) {
        try {
            Vacations nuevaVacacion = vacationService.createVacation(vacation);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVacacion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vacations> updateVacation(
            @PathVariable Long id,
            @RequestBody Vacations vacacionActualizada) {
        try {
            Optional<Vacations> vacacion = vacationService.updateVacation(id, vacacionActualizada);
            if (vacacion.isPresent()) {
                return ResponseEntity.ok(vacacion.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVacation(@PathVariable Long id) {
        try {
            boolean eliminado = vacationService.deleteVacation(id);
            if (eliminado) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Vacation service is running!");
    }
}

package com.familyspencesapi.controllers.vacation;

import com.familyspencesapi.domain.vacation.Vacation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/vacations")
@CrossOrigin(origins = "*")
public class VacationController {

    private final Map<Long, Vacation> vacations = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostMapping
    public ResponseEntity<Vacation> createVacation(@RequestBody Vacation vacation) {
        try {
            Long newId = idGenerator.getAndIncrement();
            vacation.setId(newId);

            vacations.put(newId, vacation);

            return ResponseEntity.status(HttpStatus.CREATED).body(vacation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vacation> updateVacation(
            @PathVariable Long id,
            @RequestBody Vacation vacacionActualizada) {
        try {
            if (vacations.containsKey(id)) {
                vacacionActualizada.setId(id);

                vacations.put(id, vacacionActualizada);

                return ResponseEntity.ok(vacacionActualizada);
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
            Vacation removedVacation = vacations.remove(id);

            if (removedVacation != null) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vacation> obtainVacation(@PathVariable Long id) {
        try {
            Vacation vacation = vacations.get(id);
            if (vacation != null) {
                return ResponseEntity.ok(vacation);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Map<Long, Vacation>> getAllVacations() {
        try {
            return ResponseEntity.ok(vacations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Vacation service is running!");
    }
}
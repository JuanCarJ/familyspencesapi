package com.familyspencesapi.controllers.home;

import com.familyspencesapi.domain.home.HomeSummary;
import com.familyspencesapi.service.home.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getHomeSummary(@RequestParam String userId) {
        try {
            UUID uuid = UUID.fromString(userId); // valida que sea UUID
            HomeSummary summary = homeService.getHomeSummary(uuid.toString());
            return ResponseEntity.ok(Map.of(
                    "totalIncomes", summary.getTotalIncomes(),
                    "totalExpenses", summary.getTotalExpenses(),
                    "generalBalance", summary.getGeneralBalance(),
                    "lastIncome", summary.getLastIncome()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "UUID inválido",
                    "message", "El userId proporcionado no es un UUID válido"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error interno del servidor",
                    "message", e.getMessage()
            ));
        }
    }
}

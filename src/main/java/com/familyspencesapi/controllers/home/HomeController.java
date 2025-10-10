package com.familyspencesapi.controllers.home;

import com.familyspencesapi.domain.home.GeneralBalance;
import com.familyspencesapi.service.home.HomeService;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/balances/{familyId}")
    public ResponseEntity<Object> getGeneralBalance(@PathVariable UUID familyId) {
        try {
            GeneralBalance balance = homeService.calculateGeneralBalance(familyId);
            return ResponseEntity.ok(balance);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", "An error occurred while calculating the balance: " + e.getMessage()));
        }
    }

    @PutMapping("/balances/monthlyclosings/{familyId}")
    public ResponseEntity<Map<String, String>> triggerMonthlyClosing(@PathVariable UUID familyId) {
        try {
            homeService.initiateMonthlyClosing(familyId);
            return ResponseEntity
                    .accepted()
                    .body(Map.of("message", "Monthly closing process initiated for family " + familyId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initiate closing process: " + e.getMessage()));
        }
    }
}
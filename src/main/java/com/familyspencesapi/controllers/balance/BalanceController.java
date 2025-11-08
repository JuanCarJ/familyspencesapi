package com.familyspencesapi.controllers.balance;

import com.familyspencesapi.domain.home.GeneralBalance;
import com.familyspencesapi.domain.home.Closings;
import com.familyspencesapi.service.balance.BalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/home")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/balances/{familyId}")
    public ResponseEntity<Object> getGeneralBalance(@PathVariable UUID familyId) {
        try {
            GeneralBalance balance = balanceService.calculateGeneralBalance(familyId);
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
            balanceService.initiateMonthlyClosing(familyId);
            return ResponseEntity
                    .accepted()
                    .body(Map.of("message", "Monthly closing process initiated for family " + familyId));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initiate closing process: " + e.getMessage()));
        }
    }

    @GetMapping("/balances/monthlyclosings/{familyId}")
    public ResponseEntity<List<Closings>> getClosingHistory(@PathVariable UUID familyId) {
        List<Closings> history = balanceService.getClosingHistoryForFamily(familyId);
        return ResponseEntity.ok(history);
    }
}
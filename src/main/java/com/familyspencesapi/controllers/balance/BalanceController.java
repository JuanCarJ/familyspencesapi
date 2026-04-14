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
    public ResponseEntity<Object> getGeneralBalance(@PathVariable String familyId) {
        try {
            UUID familyUUID = UUID.fromString(familyId);
            GeneralBalance balance = balanceService.calculateGeneralBalance(familyUUID);
            return ResponseEntity.ok(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid familyId format. Must be a valid UUID."));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("error", "An error occurred while calculating the balance: " + e.getMessage()));
        }
    }

    @PutMapping("/balances/monthlyclosings/{familyId}")
    public ResponseEntity<Map<String, String>> MonthlyClosing(
            @PathVariable String familyId,
            @RequestParam(required = false) String month) {
        try {
            UUID familyUUID = UUID.fromString(familyId);
            java.time.YearMonth targetMonth;
            if (month != null && !month.isEmpty()) {
                targetMonth = java.time.YearMonth.parse(month);
            } else {
                targetMonth = java.time.YearMonth.now();
            }

            balanceService.initiateMonthlyClosing(familyUUID, targetMonth);
            return ResponseEntity
                    .accepted()
                    .body(Map.of("message", "Monthly closing process initiated for family " + familyUUID + " for month " + targetMonth));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid familyId format. Must be a valid UUID."));
        } catch (java.time.format.DateTimeParseException e) {
             return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid month format. Use YYYY-MM."));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initiate closing process: " + e.getMessage()));
        }
    }

    @GetMapping("/balances/monthlyclosings/history/{familyId}")
    public ResponseEntity<?> getClosingHistory(@PathVariable String familyId) {
        try {
            UUID familyUUID = UUID.fromString(familyId);
            List<Closings> history = balanceService.getClosingHistoryForFamily(familyUUID);
            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid familyId format. Must be a valid UUID."));
        }
    }
}
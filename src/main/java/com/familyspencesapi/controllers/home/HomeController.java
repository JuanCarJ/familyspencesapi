package com.familyspencesapi.controllers.home;

import com.familyspencesapi.domain.home.GeneralBalance;
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


    @GetMapping("/balance/{familyId}")
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
}
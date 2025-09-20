package com.familyspencesapi.controllers.home;

import com.familyspencesapi.services.home.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    // ✅ Endpoint para obtener total de ingresos
    @GetMapping("/incomes")
    public BigDecimal getTotalIncomes() {
        return homeService.getTotalIncomes();
    }

    // ✅ Endpoint para obtener total de gastos
    @GetMapping("/expenses")
    public BigDecimal getTotalExpenses() {
        return homeService.getTotalExpenses();
    }

    // ✅ Endpoint para obtener balance general
    @GetMapping("/balance")
    public BigDecimal getBalance() {
        return homeService.getBalance();
    }

    // ✅ Endpoint para traer todo en un solo JSON
    @GetMapping("/summary")
    public Map<String, BigDecimal> getSummary() {
        Map<String, BigDecimal> summary = new HashMap<>();
        summary.put("totalIncomes", homeService.getTotalIncomes());
        summary.put("totalExpenses", homeService.getTotalExpenses());
        summary.put("balance", homeService.getBalance());
        return summary;
    }
}


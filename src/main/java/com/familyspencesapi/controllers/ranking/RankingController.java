package com.familyspencesapi.controllers.ranking;

import com.familyspencesapi.controllers.ranking.response.Response;
import com.familyspencesapi.controllers.ranking.response.SuccessfulResponse;
import com.familyspencesapi.service.ranking.RankingService;
import com.familyspencesapi.utils.RankingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/api/family")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(final RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping(value = "/rankingExcel/{familyId}/{period}")
    public ResponseEntity<byte[]> rankingReport(@PathVariable UUID familyId, @PathVariable String period) {

        try {
            Map<String, Double> expenses = rankingService.generateRankingExpenses(familyId, period);
            Map<String, Double> income = rankingService.generateRankingIncome(familyId, period);

            byte[] excel = rankingService.generateRankingExcel(familyId, expenses, income);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ranking.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);
        } catch (RankingException e) {
            throw new RankingException("Se genero un error de tipo: ", e);}
    }

    @GetMapping("/ranking/expenses/{familyId}/by-period/{period}")
    public ResponseEntity<Response> getRankingExpenses(@PathVariable UUID familyId, @PathVariable String period) {
        if (familyId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Map<String, Double> expenses = rankingService.generateRankingExpenses(familyId, period);
            return ResponseEntity.ok(new SuccessfulResponse(expenses));
        }catch (RankingException e){
            throw new RankingException("Se generó un error consultando el ranking: ",e);
        }
    }

    @GetMapping("/ranking/income/{familyId}/by-period/{period}")
    public ResponseEntity<Response> getRankingIncome(@PathVariable UUID familyId, @PathVariable String period) {
        if (familyId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Map<String, Double> income = rankingService.generateRankingIncome(familyId,period);
            return ResponseEntity.ok(new SuccessfulResponse(income));
        }catch (RankingException e){
            throw new RankingException("Se generó un error consultando el ranking: ",e);
        }
    }
    @PostMapping("/ranking/calculate/{familyId}/{period}")
    public ResponseEntity<Map<String, String>> triggerRankingCalculation(
            @PathVariable UUID familyId,
            @PathVariable String period) {

        if (familyId == null || period == null || !period.matches("\\d{4}-\\d{2}")) {
            return ResponseEntity.badRequest().body(Map.of("error", "El formato del período debe ser YYYY-MM"));
        }

        try {
            rankingService.calculateAndStoreMonthlyRanking(familyId, period);
            return ResponseEntity.ok(Map.of("message", "Cálculo del ranking para el período " + period + " iniciado."));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al calcular el ranking: " + e.getMessage()));
        }
    }
    @DeleteMapping("/ranking/delete/{familyId}/{period}")
    public ResponseEntity<Map<String, String>> deleteRanking(
            @PathVariable UUID familyId,
            @PathVariable String period) {

        rankingService.deleteRanking(familyId, period);
        return ResponseEntity.ok(Map.of("message", "Ranking eliminado exitosamente."));
    }

}




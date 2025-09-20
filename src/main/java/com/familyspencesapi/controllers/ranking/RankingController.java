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

    @PostMapping(value = "/rankingExcel/{familyId}")
    public ResponseEntity<byte[]> rankingReport(@PathVariable UUID familyId) {

        try {

            byte[] excel = rankingService.generateRankingExcel(familyId,
                    rankingService.generateRankingExpenses(familyId),
                    rankingService.generateRankingIncome(familyId));

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ranking.xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);
        } catch (RankingException e) {
            throw new RankingException("Se genero un error de tipo: ", e);}
    }

    @GetMapping("/ranking/expenses/{familyId}")
    public ResponseEntity<Response> getRankingExpenses(@PathVariable UUID familyId) {
        if (familyId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Map<String, Double> expenses = rankingService.generateRankingExpenses(familyId);
            return ResponseEntity.ok(new SuccessfulResponse(expenses));
        }catch (RankingException e){
            throw new RankingException("Se generó un error consultando el ranking: ",e);
        }
    }

    @GetMapping("/ranking/income/{familyId}")
    public ResponseEntity<Response> getRankingIncome(@PathVariable UUID familyId) {
        if (familyId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Map<String, Double> income = rankingService.generateRankingIncome(familyId);
            return ResponseEntity.ok(new SuccessfulResponse(income));
        }catch (RankingException e){
            throw new RankingException("Se generó un error consultando el ranking: ",e);
        }
    }

}

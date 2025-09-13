package com.familyspencesapi.controllers.ranking;

import com.familyspencesapi.controllers.ranking.response.FailedResponse;
import com.familyspencesapi.controllers.ranking.response.Response;
import com.familyspencesapi.controllers.ranking.response.SuccessfulResponse;
import com.familyspencesapi.service.ranking.RankingService;
import com.familyspencesapi.utils.RankingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @PostMapping(value="/ranking/{familyId}")
    public ResponseEntity<Response> rankingReport(@PathVariable UUID familyId){
        if(familyId==null){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FailedResponse("El id de laa familia no puede ser nulo "));
        }

        try {
            byte[] excel = rankingService.generateRankingExcel(familyId);
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ranking.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new SuccessfulResponse(excel));
        } catch (RankingException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FailedResponse("Error generando el ranking: " + e.getMessage()));
        }
    }



}

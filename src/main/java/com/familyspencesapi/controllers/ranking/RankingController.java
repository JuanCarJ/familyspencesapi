package com.familyspencesapi.controllers.ranking;

import com.familyspencesapi.service.ranking.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/family")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @PostMapping(value="/ranking/{idFamily}")
    public ResponseEntity<?> Ranking(@PathVariable UUID idFamily){
        if (idFamily == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "El idFamily no puede ser nulo"));
        }

        try {
            byte[] excelFile=rankingService.generateRankingExcel(idFamily);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ranking_familia_.xlsx\"")
                    .header("X-Message", "Archivo generado exitosamente")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar Excel: " + e.getMessage());
        }
    }



}

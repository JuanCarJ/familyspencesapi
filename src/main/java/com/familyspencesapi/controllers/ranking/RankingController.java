package com.familyspencesapi.controllers.ranking;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

@RestController
@RequestMapping("/api/family")
public class RankingController {

    @PostMapping(value="/ranking/{idFamily}")
    public ResponseEntity<?> Ranking(@PathVariable UUID idFamily){
        if(idFamily==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            byte[] excelFile = generateRankingExcel(idFamily);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"ranking_familia_.xlsx\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar Excel: " + e.getMessage());
        }
    }

    private byte[] generateRankingExcel(UUID familyId) throws Exception {
        // 🔹 Datos de prueba (mock)
        Map<String, Double> gastos = new HashMap<>();
        gastos.put("Juan Pérez", 1250.75);
        gastos.put("María García", 890.50);
        gastos.put("Carlos López", 2100.25);

        Map<String, Double> ingresos = new HashMap<>();
        ingresos.put("Juan Pérez", 3000.00);
        ingresos.put("María García", 2500.50);
        ingresos.put("Carlos López", 1800.75);

        try (Workbook workbook = new XSSFWorkbook()) {

            // Hoja de gastos
            Sheet sheetGastos = workbook.createSheet("Ranking Gastos");
            createRankingSheet(sheetGastos, gastos, "Total Gastado");

            // Hoja de ingresos
            Sheet sheetIngresos = workbook.createSheet("Ranking Ingresos");
            createRankingSheet(sheetIngresos, ingresos, "Total Ingresado");

            // Hoja de resumen
            Sheet sheetResumen = workbook.createSheet("Resumen Familia");
            createSummarySheet(sheetResumen, gastos, ingresos);

            // Convertir a bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Crea una hoja de ranking ordenada por valor descendente
     */
    private void createRankingSheet(Sheet sheet, Map<String, Double> data, String headerLabel) {
        int rowIdx = 0;

        Row header = sheet.createRow(rowIdx++);
        header.createCell(0).setCellValue("Nombre");
        header.createCell(1).setCellValue(headerLabel);

        List<Map.Entry<String, Double>> entries = new ArrayList<>(data.entrySet());
        entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());

        for (Map.Entry<String, Double> entry : entries) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createSummarySheet(Sheet sheet, Map<String, Double> gastos, Map<String, Double> ingresos) {
        double totalGastos = gastos.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalIngresos = ingresos.values().stream().mapToDouble(Double::doubleValue).sum();
        double balance = totalIngresos - totalGastos;

        int rowIdx = 0;

        Row row1 = sheet.createRow(rowIdx++);
        row1.createCell(0).setCellValue("Total Gastos");
        row1.createCell(1).setCellValue(totalGastos);

        Row row2 = sheet.createRow(rowIdx++);
        row2.createCell(0).setCellValue("Total Ingresos");
        row2.createCell(1).setCellValue(totalIngresos);

        Row row3 = sheet.createRow(rowIdx++);
        row3.createCell(0).setCellValue("Balance");
        row3.createCell(1).setCellValue(balance);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }


}

package com.familyspencesapi.service.ranking;

import com.familyspencesapi.utils.RankingException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.logging.Logger;



@Service
public class RankingService {

    private static final Logger logger = Logger.getLogger(RankingService.class.getName());

    public byte[] generateRankingExcel(UUID familyId)  {

        try {
            Map<String, Double> expenses = new HashMap<>();
            expenses.put("David Lopera", 10000000.25);
            expenses.put("Farid Sanchez", 0.50);
            expenses.put("Jeronimoq18", 2100.25);
            expenses.put("Juan Pablo Aristizabal", 300.00);

            Map<String, Double> earnings = new HashMap<>();
            earnings.put("David Lopera", 0.25);
            earnings.put("Farid Sanchez", 0.50);
            earnings.put("Jeronimoq18", 2500.25);
            earnings.put("Juan Pablo Aristizabal", 300000.00);

            try (Workbook workbook = new XSSFWorkbook()) {

                Sheet sheetExpenses = workbook.createSheet("Ranking Gastos");
                createRankingSheet(sheetExpenses, expenses, "Total Gastado", workbook);


                Sheet sheetEarnings = workbook.createSheet("Ranking Ingresos");
                createRankingSheet(sheetEarnings, earnings, "Total Ingresado", workbook);

                Sheet sheetSummary = workbook.createSheet("Resumen Familia");
                createSummarySheet(sheetSummary, expenses, earnings, workbook);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);

                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            logger.warning("Error al generar el archivo Excel");
            throw new RankingException("Error al generar el archivo Excel: " + e.getMessage(), e);
        }
    }

    private void createRankingSheet(Sheet sheet, Map<String, Double> data, String headerLabel, Workbook workbook) {
        try {
            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            Cell headerCell1 = header.createCell(0);
            Cell headerCell2 = header.createCell(1);
            headerCell1.setCellValue("Nombre");
            headerCell2.setCellValue(headerLabel);
            styleHeaderRow(header, workbook);

            List<Map.Entry<String, Double>> entries = new ArrayList<>(data.entrySet());
            entries.sort(Map.Entry.<String, Double>comparingByValue().reversed());

            int position = 0;
            for (Map.Entry<String, Double> entry : entries) {
                Row row = sheet.createRow(rowIdx++);
                Cell nameCell = row.createCell(0);
                Cell valueCell = row.createCell(1);
                nameCell.setCellValue(entry.getKey());
                valueCell.setCellValue(entry.getValue());

                styleDataRow(row, workbook, position);
                position++;
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);



        } catch (Exception e) {
            logger.warning("Error al crear hoja de ranking");
            throw new RankingException("Error al crear hoja de ranking: " + e.getMessage(), e);
        }
    }

    private void createSummarySheet(Sheet sheet, Map<String, Double> expenses, Map<String, Double> earnings, Workbook workbook) {
        try {
            double totalExpenses = expenses.values().stream().mapToDouble(Double::doubleValue).sum();
            double totalEarnings = earnings.values().stream().mapToDouble(Double::doubleValue).sum();
            double balance = totalEarnings - totalExpenses;


            Row row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue("Total Gastos");
            row1.createCell(1).setCellValue(totalExpenses);
            styleSummaryCell(row1, 0, workbook, false);
            styleSummaryCell(row1, 1, workbook, true);

            Row row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue("Total Ingresos");
            row2.createCell(1).setCellValue(totalEarnings);
            styleSummaryCell(row2, 0, workbook, false);
            styleSummaryCell(row2, 1, workbook, true);

            Row row3 = sheet.createRow(2);
            row3.createCell(0).setCellValue("Balance");
            row3.createCell(1).setCellValue(balance);
            styleSummaryCell(row3, 0, workbook, false);
            styleBalanceCell(row3, workbook, balance);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

        } catch (Exception e) {
            logger.warning("Error al crear hoja de resumen");
            throw new RankingException("Error al crear hoja de resumen: " + e.getMessage(), e);
        }
    }

    private void styleHeaderRow(Row header, Workbook workbook) {
        try {
            CellStyle headerStyle = workbook.createCellStyle();

            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 12);
            headerStyle.setFont(font);

            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < header.getLastCellNum(); i++) {
                Cell cell = header.getCell(i);
                if (cell != null) {
                    cell.setCellStyle(headerStyle);
                }
            }
        } catch (Exception e) {
            logger.warning("Error al aplicar estilo al encabezado");
            throw  new RankingException("Error al aplicar estilo al encabezado: " + e.getMessage());
        }
    }

    private void styleDataRow(Row row, Workbook workbook, int position) {
        try {
            CellStyle dataStyle = workbook.createCellStyle();

            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CreationHelper createHelper = workbook.getCreationHelper();
            dataStyle.setDataFormat(createHelper.createDataFormat().getFormat(Constant.EXCEL_CURRENCY_FORMAT));

            if (position % 2 == 0) {
                dataStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            } else {
                dataStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            }
            dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    cell.setCellStyle(dataStyle);
                }
            }
        } catch (Exception e) {
            logger.warning("Error al aplicar estilo a fila de datos");
            throw new RankingException("Error al aplicar estilo a fila de datos: " + e.getMessage());
        }
    }

    private void styleSummaryCell(Row row, int cellIndex, Workbook workbook, boolean isValueCell) {
        try {
            CellStyle style = workbook.createCellStyle();

            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);

            if (isValueCell) {
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);


                CreationHelper createHelper = workbook.getCreationHelper();
                style.setDataFormat(createHelper.createDataFormat().getFormat(Constant.EXCEL_CURRENCY_FORMAT));

                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);

                style.setAlignment(HorizontalAlignment.RIGHT);
            } else {

                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);

                style.setAlignment(HorizontalAlignment.LEFT);
            }

            Cell cell = row.getCell(cellIndex);
            if (cell != null) {
                cell.setCellStyle(style);
            }
        } catch (Exception e) {
            logger.warning("Error al aplicar estilo a celda de resumen");
            throw new RankingException("Error al aplicar estilo a celda de resumen: " + e.getMessage());


        }
    }

    private void styleBalanceCell(Row row, Workbook workbook, double balance) {
        try {
            CellStyle style = workbook.createCellStyle();

            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);

            CreationHelper createHelper = workbook.getCreationHelper();
            style.setDataFormat(createHelper.createDataFormat().getFormat(Constant.EXCEL_CURRENCY_FORMAT));

            if (balance >= 0) {
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            } else {
                style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            }
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short) 12);
            style.setFont(font);


            style.setAlignment(HorizontalAlignment.RIGHT);

            Cell cell = row.getCell(1);
            if (cell != null) {
                cell.setCellStyle(style);
            }
        } catch (Exception e) {
            logger.warning("Error al aplicar estilo a celda de balance");
            throw new RankingException("Error al aplicar estilo a celda de balance: " + e.getMessage());
        }
    }


}
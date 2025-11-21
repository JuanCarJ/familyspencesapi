package com.familyspencesapi.service.ranking;

import com.familyspencesapi.config.messages.budgetprocessor.ranking.RankingProcessQueueConfig;
import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.income.Income;
import com.familyspencesapi.domain.ranking.Ranking;
import com.familyspencesapi.domain.users.Family;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.messages.ranking.MessageSenderBrokerRanking;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.income.RepositoryIncome;
import com.familyspencesapi.repositories.ranking.RankingRepository;
import com.familyspencesapi.repositories.users.FamilyRepository;
import com.familyspencesapi.utils.RankingException;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final MessageSenderBrokerRanking messageSenderBrokerRanking;
    private final RankingProcessQueueConfig  rankingProcessQueueConfig;

    private static final Logger logger = Logger.getLogger(RankingService.class.getName());
    private final FamilyRepository familyRepository;
    private final ExpenseRepository expenseRepository;

    private final RankingRepository rankingRepository;
    private final RepositoryIncome incomeRepository;

    public RankingService(MessageSenderBrokerRanking messageSenderBrokerRanking, RankingProcessQueueConfig rankingProcessQueueConfig, FamilyRepository familyRepository, ExpenseRepository expenseRepository, RankingRepository rankingRepository, RepositoryIncome incomeRepository) {
        this.messageSenderBrokerRanking = messageSenderBrokerRanking;
        this.rankingProcessQueueConfig = rankingProcessQueueConfig;
        this.familyRepository = familyRepository;
        this.expenseRepository = expenseRepository;
        this.rankingRepository = rankingRepository;
        this.incomeRepository = incomeRepository;
    }

    public byte[] generateRankingExcel(UUID familyId, Map<String, Double> expenses, Map<String, Double> earnings) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheetExpenses = workbook.createSheet("Ranking Gastos");
            createRankingSheet(sheetExpenses, expenses, "Total Gastado", workbook);

            Sheet sheetEarnings = workbook.createSheet("Ranking Ingresos");
            createRankingSheet(sheetEarnings, earnings, "Total Ingresado", workbook);

            Sheet sheetSummary = workbook.createSheet("Resumen Familia");
            createSummarySheet(sheetSummary, expenses, earnings, workbook);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            logger.info("Excel creado con éxito");
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RankingException("Error al generar el archivo Excel: " + e.getMessage(), e);
        }
    }

    private void createRankingSheet(Sheet sheet, Map<String, Double> data, String headerLabel, Workbook workbook) {
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
            styleDataRow(row, workbook, position++);
        }
    }

    private void createSummarySheet(Sheet sheet, Map<String, Double> expenses, Map<String, Double> earnings, Workbook workbook) {
        double totalExpenses = expenses.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalEarnings = earnings.values().stream().mapToDouble(Double::doubleValue).sum();
        double balance = totalEarnings - totalExpenses;

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("Total Gastos");
        row1.createCell(1).setCellValue(totalExpenses);
        styleSummaryCell(row1, 0, workbook, false);
        styleSummaryCell(row1, 1, workbook, true);

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Total Ingresos");
        row2.createCell(1).setCellValue(totalEarnings);
        styleSummaryCell(row2, 0, workbook, false);
        styleSummaryCell(row2, 1, workbook, true);

        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("Balance");
        row3.createCell(1).setCellValue(balance);
        styleSummaryCell(row3, 0, workbook, false);
        styleBalanceCell(row3, workbook, balance);
    }

    private void styleHeaderRow(Row header, Workbook workbook) {
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
            if (cell != null) cell.setCellStyle(headerStyle);
        }
    }

    private void styleDataRow(Row row, Workbook workbook, int position) {
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        CreationHelper createHelper = workbook.getCreationHelper();
        dataStyle.setDataFormat(createHelper.createDataFormat().getFormat(Constant.EXCEL_CURRENCY_FORMAT));

        dataStyle.setFillForegroundColor(
                position % 2 == 0 ? IndexedColors.LIGHT_YELLOW.getIndex() : IndexedColors.WHITE.getIndex()
        );
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) cell.setCellStyle(dataStyle);
        }
    }

    private void styleSummaryCell(Row row, int cellIndex, Workbook workbook, boolean isValueCell) {
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
        if (cell != null) cell.setCellStyle(style);
    }

    private void styleBalanceCell(Row row, Workbook workbook, double balance) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);

        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat(Constant.EXCEL_CURRENCY_FORMAT));
        style.setFillForegroundColor(
                balance >= 0 ? IndexedColors.LIGHT_GREEN.getIndex() : IndexedColors.LIGHT_ORANGE.getIndex()
        );
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);

        Cell cell = row.getCell(1);
        if (cell != null) cell.setCellStyle(style);
    }

    public Map<String, Double> generateRankingExpenses(UUID familyId, String period) {
        List<Ranking> rankings = rankingRepository.findByFamilyIdAndPeriodOrderByTotalExpenses(familyId, period);


        return rankings.stream()
                .collect(Collectors.toMap(
                        Ranking::getFullName,
                        r -> r.getTotalExpenses().doubleValue(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Double> generateRankingIncome(UUID familyId, String period) {
        List<Ranking> rankings = rankingRepository.findByFamilyIdAndPeriodOrderByTotalIncome(familyId, period);

        return rankings.stream()
                .collect(Collectors.toMap(
                        Ranking::getFullName,
                        r -> r.getTotalIncome().doubleValue(),
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    private Map<String, Double> sortDescending(Map<String, Double> data) {
        return data.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(
                        LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        LinkedHashMap::putAll
                );
    }
    @Transactional
    public void calculateAndStoreMonthlyRanking(UUID familyId, String period) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new RankingException("No se encontró la familia con id: " + familyId));


        if (rankingRepository.existsByFamilyIdAndPeriod(familyId, period)) {
            logger.warning("El ranking para el periodo " + period + " y familia " + familyId + " ya fue calculado.");
            return;
        }

        List<RegisterUser> users = family.getUsers();
        if (users == null || users.isEmpty()) {
            logger.warning("No se encontraron usuarios para la familia: " + familyId);
            return;
        }
        for (RegisterUser user : users) {

            List<Expense> expensesList = expenseRepository.findByResponsibleAndPeriod(user.getEmail(), period);
            BigDecimal totalExpenses = expensesList.stream()
                    .map(Expense::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);


            List<Income> incomeList = incomeRepository.findByResponsibleIdAndPeriod(user.getId(), period);
            BigDecimal totalIncome = incomeList.stream()
                    .map(Income::getTotal)
                    .map(BigDecimal::valueOf)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Ranking rankingData = new Ranking(
                    familyId,
                    user.getId(),
                    user.getfullName(),
                    period,
                    totalExpenses,
                    totalIncome
            );

            messageSenderBrokerRanking.execute(rankingData,rankingProcessQueueConfig.getRoutingKeyCreate());

        }
        logger.info("Solicitudes de guardado de Ranking para el periodo " + period + " enviadas a RabbitMQ.");
    }
    @Transactional
    public void deleteRanking(UUID familyId, String period) {
        rankingRepository.deleteByFamilyIdAndPeriod(familyId, period);
    }

}


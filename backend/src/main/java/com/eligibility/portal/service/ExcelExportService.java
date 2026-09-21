package com.eligibility.portal.service;

import com.eligibility.portal.dto.response.HistorySummaryResponse;
import com.eligibility.portal.exception.ExportGenerationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds the Excel export of the (optionally filtered) submission history. Column widths are
 * fixed rather than auto-sized: Sheet#autoSizeColumn needs AWT font metrics, which are flaky or
 * absent on minimal headless JRE base images used for Docker deployment.
 */
@Service
public class ExcelExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String[] HEADERS = {"Student Name", "Course Applied", "Eligibility Status", "Date"};
    private static final int[] COLUMN_WIDTHS = {7000, 13000, 5500, 5500};

    public byte[] export(List<HistorySummaryResponse> rows) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Submission History");
            for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
                sheet.setColumnWidth(i, COLUMN_WIDTHS[i]);
            }

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (HistorySummaryResponse summary : rows) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(summary.getStudentName());
                row.createCell(1).setCellValue(summary.getCourseApplied());
                row.createCell(2).setCellValue(summary.getEligibilityStatus());
                row.createCell(3).setCellValue(summary.getDate().format(DATE_FORMAT));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ExportGenerationException("Failed to generate Excel export", e);
        }
    }
}

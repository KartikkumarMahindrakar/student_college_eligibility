package com.eligibility.portal.service;

import com.eligibility.portal.dto.response.HistorySummaryResponse;
import com.eligibility.portal.exception.ExportGenerationException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Builds the PDF export of the (optionally filtered) submission history via OpenPDF. */
@Service
public class PdfExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] export(List<HistorySummaryResponse> rows) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Submission History", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(16);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 4f, 2f, 2f});

            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            for (String header : new String[]{"Student Name", "Course Applied", "Status", "Date"}) {
                PdfPCell cell = new PdfPCell(new Paragraph(header, headerFont));
                cell.setPadding(6);
                table.addCell(cell);
            }

            Font bodyFont = new Font(Font.HELVETICA, 10);
            for (HistorySummaryResponse summary : rows) {
                addCell(table, summary.getStudentName(), bodyFont);
                addCell(table, summary.getCourseApplied(), bodyFont);
                addCell(table, summary.getEligibilityStatus(), bodyFont);
                addCell(table, summary.getDate().format(DATE_FORMAT), bodyFont);
            }

            document.add(table);
            return out.toByteArray();
        } catch (Exception e) {
            throw new ExportGenerationException("Failed to generate PDF export", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private void addCell(PdfPTable table, String value, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}

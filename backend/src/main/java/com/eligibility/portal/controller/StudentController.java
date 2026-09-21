package com.eligibility.portal.controller;

import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.response.EligibilityResponse;
import com.eligibility.portal.dto.response.HistoryDetailResponse;
import com.eligibility.portal.dto.response.HistorySummaryResponse;
import com.eligibility.portal.dto.response.HistorySummaryResponse;
import com.eligibility.portal.dto.response.StatisticsResponse;
import com.eligibility.portal.service.ExcelExportService;
import com.eligibility.portal.service.PdfExportService;
import com.eligibility.portal.service.StatisticsService;
import com.eligibility.portal.service.StudentEligibilityService;
import com.eligibility.portal.service.StudentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/students")
@Tag(name = "Students", description = "Eligibility submission, result lookup, and staff history/export/statistics")
public class StudentController {

    private final StudentEligibilityService eligibilityService;
    private final StudentHistoryService historyService;
    private final StatisticsService statisticsService;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    public StudentController(StudentEligibilityService eligibilityService,
                              StudentHistoryService historyService,
                              StatisticsService statisticsService,
                              ExcelExportService excelExportService,
                              PdfExportService pdfExportService) {
        this.eligibilityService = eligibilityService;
        this.historyService = historyService;
        this.statisticsService = statisticsService;
        this.excelExportService = excelExportService;
        this.pdfExportService = pdfExportService;
    }

    @Operation(summary = "Submit a student's details and evaluate eligibility for their desired course")
    @PostMapping("/check-eligibility")
    public ResponseEntity<EligibilityResponse> checkEligibility(@Valid @RequestBody CheckEligibilityRequest request) {
        EligibilityResponse response = eligibilityService.checkEligibility(request);
        return ResponseEntity.created(URI.create("/students/check-eligibility/" + response.getId())).body(response);
    }

    @Operation(summary = "Re-fetch a previously computed result (public - backs the Result screen's refresh/deep-link)")
    @GetMapping("/check-eligibility/{id}")
    public ResponseEntity<EligibilityResponse> getResult(@PathVariable Long id) {
        return ResponseEntity.ok(eligibilityService.getResultById(id));
    }

    @Operation(summary = "Search/filter submission history (staff only)")
    @GetMapping("/history")
    public ResponseEntity<List<HistorySummaryResponse>> getHistory(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Boolean eligible = parseStatus(status);
        return ResponseEntity.ok(historyService.search(name, course, eligible, startOfDay(from), endOfDay(to)));
    }

    @Operation(summary = "Full detail for one submission (staff only)")
    @GetMapping("/history/{id}")
    public ResponseEntity<HistoryDetailResponse> getHistoryDetail(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.getDetail(id));
    }

    @Operation(summary = "Export the (optionally filtered) submission history as an Excel workbook (staff only)")
    @GetMapping("/history/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<HistorySummaryResponse> rows = historyService.search(name, course, parseStatus(status),
                startOfDay(from), endOfDay(to));
        byte[] content = excelExportService.export(rows);
        return fileResponse(content, "submission-history.xlsx",
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Operation(summary = "Export the (optionally filtered) submission history as a PDF (staff only)")
    @GetMapping("/history/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<HistorySummaryResponse> rows = historyService.search(name, course, parseStatus(status),
                startOfDay(from), endOfDay(to));
        byte[] content = pdfExportService.export(rows);
        return fileResponse(content, "submission-history.pdf", MediaType.APPLICATION_PDF);
    }

    @Operation(summary = "Aggregate eligibility statistics for the charts screen (staff only)")
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    private Boolean parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        if ("ELIGIBLE".equalsIgnoreCase(status)) {
            return Boolean.TRUE;
        }
        if ("NOT_ELIGIBLE".equalsIgnoreCase(status)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("status must be ELIGIBLE or NOT_ELIGIBLE");
    }

    private LocalDateTime startOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    private LocalDateTime endOfDay(LocalDate date) {
        return date != null ? date.atTime(LocalTime.MAX) : null;
    }

    private ResponseEntity<byte[]> fileResponse(byte[] content, String filename, MediaType mediaType) {
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(content);
    }
}

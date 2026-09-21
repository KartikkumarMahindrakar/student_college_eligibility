package com.eligibility.portal.controller;

import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.request.SubjectMarkRequest;
import com.eligibility.portal.dto.response.EligibilityResponse;
import com.eligibility.portal.exception.ResourceNotFoundException;
import com.eligibility.portal.security.JwtAuthenticationFilter;
import com.eligibility.portal.service.ExcelExportService;
import com.eligibility.portal.service.PdfExportService;
import com.eligibility.portal.service.StatisticsService;
import com.eligibility.portal.service.StudentEligibilityService;
import com.eligibility.portal.service.StudentHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security is disabled for this slice (addFilters = false) so these tests focus purely on
 * controller/validation/error-mapping behavior. The actual public/protected split is verified
 * separately in {@link com.eligibility.portal.integration.PortalIntegrationTest}.
 */
@WebMvcTest(controllers = StudentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentEligibilityService eligibilityService;

    @MockBean
    private StudentHistoryService historyService;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private ExcelExportService excelExportService;

    @MockBean
    private PdfExportService pdfExportService;

    private CheckEligibilityRequest validRequest() {
        CheckEligibilityRequest request = new CheckEligibilityRequest();
        request.setStudentName("Jane Doe");
        request.setAge(20);
        request.setGender("Female");
        request.setJeeQualified(true);
        request.setNeetQualified(false);
        request.setDesiredCourse("Computer Science Engineering");

        List<SubjectMarkRequest> marks = new ArrayList<>();
        marks.add(new SubjectMarkRequest("Physics", 80));
        marks.add(new SubjectMarkRequest("Chemistry", 80));
        marks.add(new SubjectMarkRequest("Mathematics", 80));
        marks.add(new SubjectMarkRequest("English", 80));
        marks.add(new SubjectMarkRequest("History", 80));
        marks.add(new SubjectMarkRequest("Geography", 80));
        request.setSubjectMarks(marks);
        return request;
    }

    @Test
    void checkEligibility_happyPath_returnsCreated() throws Exception {
        EligibilityResponse response = new EligibilityResponse(1L, "Jane Doe", "Computer Science Engineering",
                true, "Meets all eligibility criteria", Collections.emptyList(), LocalDateTime.now());
        given(eligibilityService.checkEligibility(any())).willReturn(response);

        mockMvc.perform(post("/students/check-eligibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/students/check-eligibility/1"))
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.studentName").value("Jane Doe"));
    }

    @Test
    void checkEligibility_invalidBody_returns400WithFieldErrors() throws Exception {
        CheckEligibilityRequest request = validRequest();
        request.setAge(16); // below minimum
        request.setStudentName("John123"); // fails the alphabets-and-spaces pattern

        mockMvc.perform(post("/students/check-eligibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.age").exists())
                .andExpect(jsonPath("$.fieldErrors.studentName").exists());
    }

    @Test
    void getResult_unknownId_returns404() throws Exception {
        willThrow(new ResourceNotFoundException("No submission found with id 999"))
                .given(eligibilityService).getResultById(999L);

        mockMvc.perform(get("/students/check-eligibility/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No submission found with id 999"));
    }

    @Test
    void exportExcel_returnsSpreadsheetContentTypeAndAttachmentFilename() throws Exception {
        given(historyService.search(any(), any(), any(), any(), any())).willReturn(Collections.emptyList());
        given(excelExportService.export(any())).willReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/students/history/export/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", containsString("submission-history.xlsx")));
    }

    @Test
    void exportPdf_returnsPdfContentTypeAndAttachmentFilename() throws Exception {
        given(historyService.search(any(), any(), any(), any(), any())).willReturn(Collections.emptyList());
        given(pdfExportService.export(any())).willReturn(new byte[]{1, 2, 3});

        mockMvc.perform(get("/students/history/export/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("submission-history.pdf")));
    }

    @Test
    void getHistory_invalidStatusParam_returns400() throws Exception {
        mockMvc.perform(get("/students/history").param("status", "MAYBE"))
                .andExpect(status().isBadRequest());
    }
}

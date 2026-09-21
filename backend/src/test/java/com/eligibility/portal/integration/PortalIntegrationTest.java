package com.eligibility.portal.integration;

import com.eligibility.portal.dto.request.CheckEligibilityRequest;
import com.eligibility.portal.dto.request.LoginRequest;
import com.eligibility.portal.dto.request.SubjectMarkRequest;
import com.eligibility.portal.dto.response.EligibilityResponse;
import com.eligibility.portal.dto.response.HistoryDetailResponse;
import com.eligibility.portal.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full-stack round trips through the real security filter chain - this is what guards the
 * public/protected split in SecurityConfig against silent regressions, which the security-
 * disabled @WebMvcTest slices intentionally don't cover.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PortalIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void disableOutputStreaming() {
        // Default JDK HttpURLConnection streaming mode can't replay a POST body after a 401
        // challenge (HttpRetryException: "cannot retry due to server authentication, in
        // streaming mode") - buffering the body up front avoids that entirely.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setOutputStreaming(false);
        restTemplate.getRestTemplate().setRequestFactory(factory);
    }

    private CheckEligibilityRequest eligibleEngineeringRequest() {
        CheckEligibilityRequest request = new CheckEligibilityRequest();
        request.setStudentName("Integration Test");
        request.setAge(19);
        request.setGender("Other");
        request.setJeeQualified(true);
        request.setNeetQualified(false);
        request.setDesiredCourse("Computer Science Engineering");

        List<SubjectMarkRequest> marks = new ArrayList<>();
        marks.add(new SubjectMarkRequest("Physics", 90));
        marks.add(new SubjectMarkRequest("Chemistry", 90));
        marks.add(new SubjectMarkRequest("Mathematics", 90));
        marks.add(new SubjectMarkRequest("English", 60));
        marks.add(new SubjectMarkRequest("History", 60));
        marks.add(new SubjectMarkRequest("Geography", 60));
        request.setSubjectMarks(marks);
        return request;
    }

    @Test
    void publicEndpointsWorkWithoutAuthentication() {
        assertThat(restTemplate.getForEntity("/courses", String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(restTemplate.getForEntity("/subjects", String.class).getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<EligibilityResponse> submitResponse = restTemplate.postForEntity(
                "/students/check-eligibility", eligibleEngineeringRequest(), EligibilityResponse.class);
        assertThat(submitResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(submitResponse.getBody()).isNotNull();
        assertThat(submitResponse.getBody().isEligible()).isTrue();

        Long id = submitResponse.getBody().getId();
        ResponseEntity<EligibilityResponse> fetchResponse = restTemplate.getForEntity(
                "/students/check-eligibility/" + id, EligibilityResponse.class);
        assertThat(fetchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetchResponse.getBody().getStudentName()).isEqualTo("Integration Test");
    }

    @Test
    void protectedEndpointsRejectRequestsWithoutAToken() {
        assertThat(restTemplate.getForEntity("/students/history", String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(restTemplate.getForEntity("/students/statistics", String.class).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void endToEnd_submitThenLoginThenFetchHistoryDetailWithRealToken() {
        ResponseEntity<EligibilityResponse> submitResponse = restTemplate.postForEntity(
                "/students/check-eligibility", eligibleEngineeringRequest(), EligibilityResponse.class);
        Long id = submitResponse.getBody().getId();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("Admin@123");
        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity(
                "/auth/login", loginRequest, LoginResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String token = loginResponse.getBody().getToken();
        assertThat(token).isNotBlank();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<HistoryDetailResponse> detailResponse = restTemplate.exchange(
                "/students/history/" + id, HttpMethod.GET, new HttpEntity<>(headers), HistoryDetailResponse.class);
        assertThat(detailResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(detailResponse.getBody().getStudentName()).isEqualTo("Integration Test");
        assertThat(detailResponse.getBody().isEligible()).isTrue();
    }

    @Test
    void login_wrongPassword_returnsUnauthorized() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("wrong-password");
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", loginRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

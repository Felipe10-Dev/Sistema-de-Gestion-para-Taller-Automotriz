package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void login_WithValidCredentials_ReturnsTokens() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.accessToken")).isNotBlank();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.refreshToken")).isNotBlank();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.tokenType")).isEqualTo("Bearer");
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.rol")).isEqualTo("ADMIN");
    }

    @Test
    void login_WithInvalidPassword_Returns401() {
        String port = Integer.toString(restTemplate.getRestTemplate().getUriTemplateHandler()
                .expand("/").getPort());
        RestTemplate simple = new RestTemplate();
        simple.setRequestFactory(new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(
                org.apache.hc.client5.http.impl.classic.HttpClients.createDefault()));
        simple.setUriTemplateHandler(new org.springframework.web.util.DefaultUriBuilderFactory(
                "http://localhost:" + port));
        simple.setErrorHandler(new org.springframework.web.client.ResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }
            @Override
            public void handleError(org.springframework.http.client.ClientHttpResponse response) {
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{\"username\":\"admin\",\"password\":\"wrongpass\"}", headers);

        ResponseEntity<String> response = simple.exchange(
                "/api/auth/login", HttpMethod.POST, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void refresh_WithValidRefreshToken_ReturnsNewTokens() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String loginBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginBody, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, String.class);
        String refreshToken = JsonPath.read(loginResponse.getBody(), "$.data.refreshToken");

        HttpEntity<String> refreshRequest = new HttpEntity<>(
                "{\"refreshToken\":\"" + refreshToken + "\"}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/refresh", refreshRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.accessToken")).isNotBlank();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.refreshToken")).isNotBlank();
    }

    @Test
    void register_NewUser_Returns201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String uniqueUser = "testuser_" + System.currentTimeMillis();
        String body = String.format(
                "{\"username\":\"%s\",\"password\":\"123456\",\"nombre\":\"Test\",\"apellido\":\"User\",\"email\":\"%s@test.com\"}",
                uniqueUser, uniqueUser);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.username")).isEqualTo(uniqueUser);
    }
}

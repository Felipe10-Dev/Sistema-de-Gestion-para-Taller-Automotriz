package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteVehiculoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String accessToken;

    @BeforeEach
    void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);
        accessToken = JsonPath.read(response.getBody(), "$.data.accessToken");
    }

    @Test
    void createCliente_WithValidData_Returns201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        String uniqueDoc = "DNI" + System.currentTimeMillis();
        String body = String.format(
                "{\"tipoDocumento\":\"CC\",\"numeroDocumento\":\"%s\",\"nombre\":\"Juan\",\"apellido\":\"Perez\",\"telefono\":\"3001234567\",\"email\":\"juan@test.com\",\"direccion\":\"Cra 1 #2-3\"}",
                uniqueDoc);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/clientes", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
    }

    @Test
    void getClientes_WithAuth_ReturnsList() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/clientes?page=0&size=10", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void getClientes_WithoutAuth_Returns401() {
        String port = Integer.toString(restTemplate.getRestTemplate().getUriTemplateHandler()
                .expand("/").getPort());
        org.springframework.web.client.RestTemplate simple = new org.springframework.web.client.RestTemplate();
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
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = simple.exchange(
                "/api/clientes", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createVehiculo_WithValidData_Returns201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        String uniquePlaca = "XYZ" + (System.currentTimeMillis() % 100000);
        String body = String.format(
                "{\"placa\":\"%s\",\"marca\":\"Toyota\",\"linea\":\"Corolla\",\"modelo\":\"2020\",\"color\":\"Rojo\",\"cilindraje\":\"1800\",\"tipoVehiculo\":\"Sedan\",\"clienteId\":1}",
                uniquePlaca);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/vehiculos", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
    }
}

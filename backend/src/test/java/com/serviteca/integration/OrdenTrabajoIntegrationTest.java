package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import static org.assertj.core.api.Assertions.assertThat;

class OrdenTrabajoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String accessToken;
    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(
                new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{\"username\":\"admin\",\"password\":\"admin123\"}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);
        accessToken = JsonPath.read(response.getBody(), "$.data.accessToken");

        authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(accessToken);
    }

    @Test
    void createOrden_WithValidData_Returns201() {
        HttpEntity<String> request = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000,\"tecnicoId\":1,\"observaciones\":\"Prueba\",\"servicios\":[{\"servicioId\":1,\"cantidad\":1,\"observaciones\":\"\"}],\"productos\":[{\"productoId\":1,\"cantidad\":1}]}",
                authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/ordenes", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.estado")).isEqualTo("PENDIENTE");
    }

    @Test
    void closeOrden_CompletesFullFlow() {
        // Create
        HttpEntity<String> createRequest = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000,\"tecnicoId\":1,\"observaciones\":\"Completa\",\"servicios\":[{\"servicioId\":1,\"cantidad\":1}],\"productos\":[{\"productoId\":1,\"cantidad\":1}]}",
                authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/ordenes", createRequest, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number idNum = JsonPath.read(createResponse.getBody(), "$.data.id");
        Long ordenId = idNum.longValue();

        // PENDIENTE -> EN_DIAGNOSTICO
        HttpEntity<String> diagRequest = new HttpEntity<>(
                "{\"estado\":\"EN_DIAGNOSTICO\",\"observaciones\":\"Iniciando diagn\u00f3stico\"}", authHeaders);
        ResponseEntity<String> diagResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, diagRequest, String.class);
        assertThat(diagResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(diagResponse.getBody(), "$.data.estado")).isEqualTo("EN_DIAGNOSTICO");

        // EN_DIAGNOSTICO -> EN_PROCESO
        HttpEntity<String> procRequest = new HttpEntity<>(
                "{\"estado\":\"EN_PROCESO\",\"observaciones\":\"En reparaci\u00f3n\"}", authHeaders);
        ResponseEntity<String> procResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, procRequest, String.class);
        assertThat(procResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(procResponse.getBody(), "$.data.estado")).isEqualTo("EN_PROCESO");

        // EN_PROCESO -> LISTO_PARA_ENTREGA
        HttpEntity<String> listoRequest = new HttpEntity<>(
                "{\"estado\":\"LISTO_PARA_ENTREGA\",\"observaciones\":\"Trabajo terminado\"}", authHeaders);
        ResponseEntity<String> listoResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, listoRequest, String.class);
        assertThat(listoResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(listoResponse.getBody(), "$.data.estado")).isEqualTo("LISTO_PARA_ENTREGA");

        // LISTO_PARA_ENTREGA -> ENTREGADO
        HttpEntity<String> entregaRequest = new HttpEntity<>(
                "{\"estado\":\"ENTREGADO\",\"observaciones\":\"Entregado al cliente\"}", authHeaders);
        ResponseEntity<String> entregaResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, entregaRequest, String.class);
        assertThat(entregaResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(entregaResponse.getBody(), "$.data.estado")).isEqualTo("ENTREGADO");
        assertThat(JsonPath.<String>read(entregaResponse.getBody(), "$.data.fechaSalida")).isNotNull();

        // Check historial is present (should have 4 entries + initial = 5 actually... wait, initial is not recorded, only transitions)
        // Actually historial records each change: PENDIENTE->EN_DIAGNOSTICO, EN_DIAGNOSTICO->EN_PROCESO, EN_PROCESO->LISTO_PARA_ENTREGA, LISTO_PARA_ENTREGA->ENTREGADO = 4
        assertThat(JsonPath.<Integer>read(entregaResponse.getBody(), "$.data.historial.length()")).isEqualTo(4);
    }

    @Test
    void invalidStateTransition_Returns400() {
        HttpEntity<String> createRequest = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000,\"servicios\":[{\"servicioId\":1,\"cantidad\":1}]}",
                authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/ordenes", createRequest, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number idNum = JsonPath.read(createResponse.getBody(), "$.data.id");
        Long ordenId = idNum.longValue();

        // Try PENDIENTE -> ENTREGADO (invalid - should go through EN_DIAGNOSTICO, EN_PROCESO, LISTO_PARA_ENTREGA)
        HttpEntity<String> badRequest = new HttpEntity<>(
                "{\"estado\":\"ENTREGADO\"}", authHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, badRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void blockEditOnTerminal_Returns400() {
        HttpEntity<String> createRequest = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000,\"servicios\":[{\"servicioId\":1,\"cantidad\":1}],\"productos\":[{\"productoId\":1,\"cantidad\":1}]}",
                authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/ordenes", createRequest, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number idNum = JsonPath.read(createResponse.getBody(), "$.data.id");
        Long ordenId = idNum.longValue();

        // Cancel the order (terminal state)
        HttpEntity<String> cancelRequest = new HttpEntity<>(
                "{\"estado\":\"CANCELADO\",\"observaciones\":\"Cliente cancel\u00f3\"}", authHeaders);
        ResponseEntity<String> cancelResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, cancelRequest, String.class);
        assertThat(cancelResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Try to update the cancelled order -> should fail
        HttpEntity<String> updateRequest = new HttpEntity<>(
                "{\"observaciones\":\"Intento de edici\u00f3n\"}", authHeaders);
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId, HttpMethod.PUT, updateRequest, String.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Try to delete the cancelled order -> should fail
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId, HttpMethod.DELETE, new HttpEntity<>(authHeaders), String.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void addObservacion_ReturnsOk() {
        HttpEntity<String> createRequest = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000,\"servicios\":[{\"servicioId\":1,\"cantidad\":1}]}",
                authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/ordenes", createRequest, String.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number idNum = JsonPath.read(createResponse.getBody(), "$.data.id");
        Long ordenId = idNum.longValue();

        // Add observacion
        HttpEntity<String> obsRequest = new HttpEntity<>(
                "{\"comentario\":\"Cliente llam\u00f3 para preguntar estado\"}", authHeaders);
        ResponseEntity<String> obsResponse = restTemplate.exchange(
                "/api/ordenes/" + ordenId + "/observaciones", HttpMethod.POST, obsRequest, String.class);
        assertThat(obsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(obsResponse.getBody(), "$.data.observacionesList.length()")).isEqualTo(1);
        assertThat(JsonPath.<String>read(obsResponse.getBody(), "$.data.observacionesList[0].comentario"))
                .isEqualTo("Cliente llam\u00f3 para preguntar estado");
    }

    @Test
    void healthEndpoint_Returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

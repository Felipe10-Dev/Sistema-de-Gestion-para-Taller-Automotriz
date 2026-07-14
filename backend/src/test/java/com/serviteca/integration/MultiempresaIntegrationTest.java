package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class MultiempresaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String accessToken;
    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
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
    void crearYConsultarEmpresa_ReturnsOk() {
        String body = "{\"nombre\":\"Empresa Test\",\"razonSocial\":\"Test SAS\",\"nit\":\"900999999-9\"}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/multiempresa/empresas", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Number>read(response.getBody(), "$.id")).isNotNull();
        assertThat(JsonPath.<String>read(response.getBody(), "$.nombre")).isEqualTo("Empresa Test");

        Long id = ((Number) JsonPath.read(response.getBody(), "$.id")).longValue();

        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/api/multiempresa/empresas/" + id, HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(getResponse.getBody(), "$.nit")).isEqualTo("900999999-9");
    }

    @Test
    void crearSede_ReturnsOk() {
        String empBody = "{\"nombre\":\"Empresa Con Sede\",\"razonSocial\":\"Sede SAS\",\"nit\":\"900888888-8\"}";
        HttpEntity<String> empReq = new HttpEntity<>(empBody, authHeaders);
        ResponseEntity<String> empRes = restTemplate.postForEntity(
                "/api/multiempresa/empresas", empReq, String.class);
        Long empresaId = ((Number) JsonPath.read(empRes.getBody(), "$.id")).longValue();

        String sedeBody = "{\"nombre\":\"Sede Norte\",\"direccion\":\"Calle 1 #2-3\"}";
        HttpEntity<String> sedeReq = new HttpEntity<>(sedeBody, authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/multiempresa/empresas/" + empresaId + "/sedes", sedeReq, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<String>read(response.getBody(), "$.nombre")).isEqualTo("Sede Norte");
        assertThat(((Number) JsonPath.read(response.getBody(), "$.empresaId")).longValue()).isEqualTo(empresaId);
    }

    @Test
    void listarSedes_ReturnsList() {
        String empBody = "{\"nombre\":\"Empresa Listado\",\"razonSocial\":\"Listado SAS\",\"nit\":\"900777777-7\"}";
        HttpEntity<String> empReq = new HttpEntity<>(empBody, authHeaders);
        ResponseEntity<String> empRes = restTemplate.postForEntity(
                "/api/multiempresa/empresas", empReq, String.class);
        Long empresaId = ((Number) JsonPath.read(empRes.getBody(), "$.id")).longValue();

        restTemplate.postForEntity("/api/multiempresa/empresas/" + empresaId + "/sedes",
                new HttpEntity<>("{\"nombre\":\"Sede 1\"}", authHeaders), String.class);
        restTemplate.postForEntity("/api/multiempresa/empresas/" + empresaId + "/sedes",
                new HttpEntity<>("{\"nombre\":\"Sede 2\"}", authHeaders), String.class);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/multiempresa/empresas/" + empresaId + "/sedes", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.length()")).isEqualTo(2);
    }

    @Test
    void eliminarEmpresa_SoftDelete_ReturnsNoContent() {
        String body = "{\"nombre\":\"Empresa A Eliminar\",\"razonSocial\":\"Delete SAS\",\"nit\":\"900666666-6\"}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createRes = restTemplate.postForEntity(
                "/api/multiempresa/empresas", request, String.class);
        Long id = ((Number) JsonPath.read(createRes.getBody(), "$.id")).longValue();

        ResponseEntity<Void> deleteRes = restTemplate.exchange(
                "/api/multiempresa/empresas/" + id, HttpMethod.DELETE,
                new HttpEntity<>(authHeaders), Void.class);
        assertThat(deleteRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getRes = restTemplate.exchange(
                "/api/multiempresa/empresas/" + id, HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(getRes.getBody(), "$.activo")).isFalse();
    }

    @Test
    void dashboardConsolidado_ReturnsCounts() {
        String body = "{\"nombre\":\"Empresa Dash\",\"razonSocial\":\"Dash SAS\",\"nit\":\"900555555-5\"}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createRes = restTemplate.postForEntity(
                "/api/multiempresa/empresas", request, String.class);
        Long empresaId = ((Number) JsonPath.read(createRes.getBody(), "$.id")).longValue();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/multiempresa/dashboard/" + empresaId, HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Object>read(response.getBody(), "$.totalClientes")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.totalVehiculos")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.ordenesActivas")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.ordenesPendientesPago")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.totalIngresos")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.totalProductos")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.totalProveedores")).isNotNull();
    }

    @Test
    void crearTransferencia_RequiereAuth() {
        HttpHeaders noAuth = new HttpHeaders();
        noAuth.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{\"sedeOrigenId\":1,\"sedeDestinoId\":1,\"productos\":[]}", noAuth);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/multiempresa/transferencias", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}

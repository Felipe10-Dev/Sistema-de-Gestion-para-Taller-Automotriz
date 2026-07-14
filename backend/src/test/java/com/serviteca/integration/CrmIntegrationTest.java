package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class CrmIntegrationTest extends AbstractIntegrationTest {

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
    void perfil_DevuelveDatosCliente() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/1/perfil", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isEqualTo(1);
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clasificacion")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.totalOrdenes")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.totalVehiculos")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.totalGastado")).isNotNull();
    }

    @Test
    void historialEconomico_DevuelveDatos() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/1/economico", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.totalInvertido")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.cantidadOrdenes")).isNotNull();
    }

    @Test
    void historialOrdenes_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/1/ordenes", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data")).isNotNull();
    }

    @Test
    void agregarNota_Devuelve201() {
        String body = "{\"comentario\":\"Cliente solicitó cotización de frenos\"}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/crm/clientes/1/notas", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.comentario"))
                .isEqualTo("Cliente solicitó cotización de frenos");
    }

    @Test
    void listarNotas_DevuelveLista() {
        String body = "{\"comentario\":\"Nota de prueba\"}";
        restTemplate.postForEntity("/api/crm/clientes/1/notas",
                new HttpEntity<>(body, authHeaders), String.class);

        HttpEntity<String> request = new HttpEntity<>(authHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/1/notas", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.length()")).isNotNull();
    }

    @Test
    void ranking_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/ranking?tipo=invertido", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void clientesInactivos_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/inactivos?meses=12", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void clientesVip_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/vip", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void dashboardCrm_DevuelveIndicadores() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/dashboard", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clientesNuevosEsteMes")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clientesActivos")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clientesFrecuentes")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clientesVip")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clientesInactivos12Meses")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.clientesConMantenimientoProximo")).isNotNull();
    }

    @Test
    void proximosMantenimientosCliente_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/crm/clientes/1/proximos-mantenimientos", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }
}

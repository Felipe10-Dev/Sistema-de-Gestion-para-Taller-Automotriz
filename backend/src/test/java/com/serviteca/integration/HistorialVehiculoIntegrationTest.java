package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class HistorialVehiculoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String accessToken;

    @BeforeEach
    void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                "{\"username\":\"admin\",\"password\":\"admin123\"}", headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/login", request, String.class);
        accessToken = JsonPath.read(response.getBody(), "$.data.accessToken");
    }

    @Test
    void obtenerHistorial_DevuelveLista() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/vehiculos/1/historial", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void obtenerLineaTiempo_DevuelveLista() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/vehiculos/1/linea-tiempo", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void obtenerEstadisticas_DevuelveDatos() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/vehiculos/1/estadisticas", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.totalVisitas")).isNotNull();
    }

    @Test
    void crearRecomendacion_Devuelve201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        String body = "{\"tipo\":\"ACEITE\",\"tipoProgramacion\":\"AMBOS\",\"intervaloKilometraje\":5000,\"intervaloDias\":180}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/vehiculos/1/mantenimientos", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
    }

    @Test
    void listarRecomendaciones_DevuelveLista() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/vehiculos/1/mantenimientos", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void proximosMantenimientos_DevuelveLista() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/vehiculos/1/proximos-mantenimientos", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void dashboard_IncluyeIndicadoresVehiculo() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/dashboard", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.vehiculosProximosMantenimiento")).isNotNull();
    }

    @Test
    void createVehiculo_WithAllFields_Returns201() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        String uniquePlaca = "FIC" + (System.currentTimeMillis() % 100000);
        String body = String.format(
                "{\"placa\":\"%s\",\"marca\":\"Toyota\",\"linea\":\"Corolla\",\"modelo\":\"2020\"," +
                "\"color\":\"Rojo\",\"cilindraje\":\"1800\",\"tipoVehiculo\":\"Sedan\"," +
                "\"motor\":\"2ZR-FE\",\"combustible\":\"Gasolina\",\"vin\":\"JTDBR923456789012\",\"anio\":2020," +
                "\"clienteId\":1}", uniquePlaca);
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/vehiculos", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.motor")).isEqualTo("2ZR-FE");
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.combustible")).isEqualTo("Gasolina");
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.vin")).isEqualTo("JTDBR923456789012");
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.data.anio")).isEqualTo(2020);
    }
}

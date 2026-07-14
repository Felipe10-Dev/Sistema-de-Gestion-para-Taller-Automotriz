package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiguracionIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> loginReq = new HttpEntity<>(
                "{\"username\":\"admin\",\"password\":\"admin123\"}", headers);
        ResponseEntity<String> loginRes = restTemplate.postForEntity("/api/auth/login", loginReq, String.class);
        String token = JsonPath.read(loginRes.getBody(), "$.data.accessToken");

        authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(token);
    }

    @Test
    void empresa_ObtenerDevuelveDatos() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/empresa", HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Object>read(response.getBody(), "$.id")).isEqualTo(1);
        assertThat(JsonPath.<String>read(response.getBody(), "$.nombre")).isEqualTo("Mi Serviteca");
        assertThat(JsonPath.<String>read(response.getBody(), "$.moneda")).isEqualTo("COP");
    }

    @Test
    void empresa_ActualizarDevuelveDatosModificados() {
        String body = "{\"nombre\":\"Nuevo Nombre\",\"telefono\":\"1234567\"}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/empresa", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.nombre")).isEqualTo("Nuevo Nombre");
        assertThat(JsonPath.<String>read(response.getBody(), "$.telefono")).isEqualTo("1234567");
    }

    @Test
    void parametros_ListarDevuelveLista() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/parametros", HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.length()")).isGreaterThan(0);
    }

    @Test
    void parametros_ObtenerPorCodigoDevuelveDatos() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/parametros/IVA_DEFECTO", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.codigo")).isEqualTo("IVA_DEFECTO");
        assertThat(JsonPath.<String>read(response.getBody(), "$.valor")).isEqualTo("19");
    }

    @Test
    void parametros_CrearYActualizar() {
        String crear = "{\"codigo\":\"TEST_PARAM\",\"nombre\":\"Prueba\",\"valor\":\"100\",\"tipo\":\"INTEGER\"}";
        HttpEntity<String> reqCrear = new HttpEntity<>(crear, authHeaders);

        ResponseEntity<String> resCrear = restTemplate.exchange(
                "/api/configuracion/parametros", HttpMethod.POST, reqCrear, String.class);

        assertThat(resCrear.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer id = JsonPath.read(resCrear.getBody(), "$.id");

        String actualizar = "{\"valor\":\"200\"}";
        HttpEntity<String> reqAct = new HttpEntity<>(actualizar, authHeaders);
        ResponseEntity<String> resAct = restTemplate.exchange(
                "/api/configuracion/parametros/" + id, HttpMethod.PUT, reqAct, String.class);

        assertThat(resAct.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(resAct.getBody(), "$.valor")).isEqualTo("200");
    }

    @Test
    void numeracion_ListarDevuelveLista() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/numeracion", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.length()")).isGreaterThan(0);
    }

    @Test
    void numeracion_GenerarNumeroDevuelveString() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/numeracion/ORDEN_TRABAJO/generar",
                HttpMethod.POST, new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("ORD-");
    }

    @Test
    void impuestos_ListarDevuelveLista() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/impuestos", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.length()")).isGreaterThan(0);
    }

    @Test
    void horarios_ListarDevuelveLista() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/horarios", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.length()")).isEqualTo(7);
    }

    @Test
    void auditoria_ListarDevuelveLista() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/auditoria", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void backups_CrearYListar() {
        String body = "{\"usuario\":\"admin\",\"tamanio\":\"1.5 MB\",\"estado\":\"EXITOSO\"}";
        HttpEntity<String> reqCrear = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> resCrear = restTemplate.exchange(
                "/api/configuracion/backups", HttpMethod.POST, reqCrear, String.class);

        assertThat(resCrear.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<String>read(resCrear.getBody(), "$.estado")).isEqualTo("EXITOSO");

        ResponseEntity<String> resList = restTemplate.exchange(
                "/api/configuracion/backups", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(resList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(resList.getBody(), "$.length()")).isGreaterThan(0);
    }

    @Test
    void festivos_CrearYEliminar() {
        String body = "{\"fecha\":\"2026-12-25\",\"descripcion\":\"Navidad\"}";
        HttpEntity<String> reqCrear = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> resCrear = restTemplate.exchange(
                "/api/configuracion/festivos", HttpMethod.POST, reqCrear, String.class);

        assertThat(resCrear.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer id = JsonPath.read(resCrear.getBody(), "$.id");

        ResponseEntity<String> resList = restTemplate.exchange(
                "/api/configuracion/festivos", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(resList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(resList.getBody(), "$.length()")).isGreaterThan(0);

        restTemplate.exchange("/api/configuracion/festivos/" + id,
                HttpMethod.DELETE, new HttpEntity<>(authHeaders), String.class);

        ResponseEntity<String> resAfterDelete = restTemplate.exchange(
                "/api/configuracion/festivos", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        // Soft delete: la entidad persiste con activo=false
        assertThat(JsonPath.<Integer>read(resAfterDelete.getBody(), "$.length()")).isEqualTo(1);
        assertThat(JsonPath.<Object>read(resAfterDelete.getBody(), "$[0].id")).isEqualTo(id);
    }

    @Test
    void permisos_CrearListarYEliminar() {
        String body = "{\"rolId\":1,\"modulo\":\"TEST\",\"permiso\":\"LEER\"}";
        HttpEntity<String> reqCrear = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> resCrear = restTemplate.exchange(
                "/api/configuracion/permisos", HttpMethod.POST, reqCrear, String.class);

        assertThat(resCrear.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Integer id = JsonPath.read(resCrear.getBody(), "$.id");

        ResponseEntity<String> resList = restTemplate.exchange(
                "/api/configuracion/permisos?rolId=1", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);

        assertThat(resList.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Integer>read(resList.getBody(), "$.length()")).isGreaterThan(0);

        restTemplate.exchange("/api/configuracion/permisos/" + id,
                HttpMethod.DELETE, new HttpEntity<>(authHeaders), String.class);
    }

    @Test
    void horarios_ActualizarHorarioDevuelveDatos() {
        String body = "{\"horaApertura\":\"09:00\",\"horaCierre\":\"17:00\"}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/configuracion/horarios/1", HttpMethod.PUT, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.diaSemana")).isEqualTo("LUNES");
        assertThat(JsonPath.<String>read(response.getBody(), "$.horaApertura")).startsWith("09:00");
        assertThat(JsonPath.<String>read(response.getBody(), "$.horaCierre")).startsWith("17:00");
    }
}

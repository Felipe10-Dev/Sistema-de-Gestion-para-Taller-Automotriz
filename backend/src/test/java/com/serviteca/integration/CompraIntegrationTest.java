package com.serviteca.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

class CompraIntegrationTest extends AbstractIntegrationTest {

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
    void crearOrden_ConProveedor_Devuelve201() {
        String body = "{\"proveedorId\":1,\"observaciones\":\"Compra semanal\",\"productos\":[{\"productoId\":1,\"cantidad\":5,\"precioUnitario\":45000},{\"productoId\":3,\"cantidad\":10,\"precioUnitario\":8500}]}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/compras", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.numeroOrden")).startsWith("OC-");
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.estado")).isEqualTo("BORRADOR");
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.total")).isNotNull();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.proveedorNombre")).isEqualTo("Distribuidora Autopartes SAS");
        assertThat(JsonPath.<Integer>read(response.getBody(), "$.data.productos.length()")).isEqualTo(2);
    }

    @Test
    void crearOrden_SinProveedor_Devuelve400() {
        String body = "{\"productos\":[{\"productoId\":2,\"cantidad\":3,\"precioUnitario\":48000}]}";
        HttpEntity<String> request = new HttpEntity<>(body, authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/compras", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAll_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data")).isNotNull();
    }

    @Test
    void cambiarEstado_Enviar_DevuelveOk() {
        String body = "{\"proveedorId\":1,\"productos\":[{\"productoId\":1,\"cantidad\":2,\"precioUnitario\":45000}]}";
        HttpEntity<String> createRequest = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/compras", createRequest, String.class);
        Integer ordenId = JsonPath.read(createResponse.getBody(), "$.data.id");

        HttpHeaders patchHeaders = new HttpHeaders();
        patchHeaders.setContentType(MediaType.APPLICATION_JSON);
        patchHeaders.setBearerAuth(accessToken);
        String patchBody = "{\"estado\":\"ENVIADA\"}";
        HttpEntity<String> patchRequest = new HttpEntity<>(patchBody, patchHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras/" + ordenId + "/estado", HttpMethod.PATCH, patchRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.estado")).isEqualTo("ENVIADA");
    }

    @Test
    void cambiarEstado_TransicionInvalida_Devuelve400() {
        String body = "{\"proveedorId\":1,\"productos\":[{\"productoId\":2,\"cantidad\":1,\"precioUnitario\":48000}]}";
        HttpEntity<String> createRequest = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/compras", createRequest, String.class);
        Integer ordenId = JsonPath.read(createResponse.getBody(), "$.data.id");

        HttpHeaders patchHeaders = new HttpHeaders();
        patchHeaders.setContentType(MediaType.APPLICATION_JSON);
        patchHeaders.setBearerAuth(accessToken);
        String patchBody = "{\"estado\":\"RECIBIDA\"}";
        HttpEntity<String> patchRequest = new HttpEntity<>(patchBody, patchHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras/" + ordenId + "/estado", HttpMethod.PATCH, patchRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void recibirMercancia_ActualizaInventario() {
        String body = "{\"proveedorId\":2,\"productos\":[{\"productoId\":5,\"cantidad\":10,\"precioUnitario\":35000}]}";
        HttpEntity<String> createRequest = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/compras", createRequest, String.class);
        Integer ordenId = JsonPath.read(createResponse.getBody(), "$.data.id");

        HttpHeaders patchHeaders = new HttpHeaders();
        patchHeaders.setContentType(MediaType.APPLICATION_JSON);
        patchHeaders.setBearerAuth(accessToken);

        String enviarBody = "{\"estado\":\"ENVIADA\"}";
        restTemplate.exchange("/api/compras/" + ordenId + "/estado", HttpMethod.PATCH,
                new HttpEntity<>(enviarBody, patchHeaders), String.class);

        String recibirBody = "{\"estado\":\"RECIBIDA\"}";
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras/" + ordenId + "/estado", HttpMethod.PATCH,
                new HttpEntity<>(recibirBody, patchHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.estado")).isEqualTo("RECIBIDA");

        HttpEntity<String> getRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> inventarioResponse = restTemplate.exchange(
                "/api/inventario/producto/5", HttpMethod.GET, getRequest, String.class);
        assertThat(JsonPath.<Integer>read(inventarioResponse.getBody(), "$.data.cantidadActual")).isGreaterThanOrEqualTo(10);
    }

    @Test
    void delete_EstadoBorrador_DevuelveOk() {
        String body = "{\"proveedorId\":1,\"productos\":[{\"productoId\":1,\"cantidad\":1,\"precioUnitario\":45000}]}";
        HttpEntity<String> createRequest = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/compras", createRequest, String.class);
        Integer ordenId = JsonPath.read(createResponse.getBody(), "$.data.id");

        HttpEntity<String> deleteRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras/" + ordenId, HttpMethod.DELETE, deleteRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }

    @Test
    void delete_EstadoEnviada_Devuelve400() {
        String body = "{\"proveedorId\":1,\"productos\":[{\"productoId\":1,\"cantidad\":1,\"precioUnitario\":45000}]}";
        HttpEntity<String> createRequest = new HttpEntity<>(body, authHeaders);
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/api/compras", createRequest, String.class);
        Integer ordenId = JsonPath.read(createResponse.getBody(), "$.data.id");

        HttpHeaders patchHeaders = new HttpHeaders();
        patchHeaders.setContentType(MediaType.APPLICATION_JSON);
        patchHeaders.setBearerAuth(accessToken);
        restTemplate.exchange("/api/compras/" + ordenId + "/estado", HttpMethod.PATCH,
                new HttpEntity<>("{\"estado\":\"ENVIADA\"}", patchHeaders), String.class);

        HttpEntity<String> deleteRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras/" + ordenId, HttpMethod.DELETE, deleteRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void recomendaciones_DevuelveLista() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/compras/recomendaciones", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data")).isNotNull();
    }

    @Test
    void dashboard_IncluyeIndicadoresInventario() {
        HttpEntity<String> request = new HttpEntity<>(authHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/dashboard", HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.productosBajoMinimo")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.productosAgotados")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.valorTotalInventario")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.comprasDelMes")).isNotNull();
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.proveedoresMasUtilizados")).isNotNull();
    }
}

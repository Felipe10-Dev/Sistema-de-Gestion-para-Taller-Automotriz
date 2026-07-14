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

class CajaPagoIntegrationTest extends AbstractIntegrationTest {

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

        // Close any open caja before each test
        ResponseEntity<String> cajaActual = restTemplate.exchange(
                "/api/caja/actual", HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);
        if (cajaActual.getStatusCode() == HttpStatus.OK) {
            try {
                Number cajaId = JsonPath.read(cajaActual.getBody(), "$.data.id");
                if (cajaId != null) {
                    HttpEntity<String> cierreReq = new HttpEntity<>(
                            "{\"montoContado\":0,\"observaciones\":\"Limpieza previa a test\"}", authHeaders);
                    restTemplate.exchange("/api/caja/" + cajaId.longValue() + "/cierre",
                            HttpMethod.POST, cierreReq, String.class);
                }
            } catch (Exception ignored) {}
        }
    }

    @Test
    void abrirCaja_CreaCajaAbierta() {
        HttpEntity<String> request = new HttpEntity<>(
                "{\"montoInicial\":50000,\"observacion\":\"Apertura diaria\"}", authHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/caja/apertura", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
        assertThat(JsonPath.<String>read(response.getBody(), "$.data.estado")).isEqualTo("ABIERTA");
        assertThat(JsonPath.<Object>read(response.getBody(), "$.data.id")).isNotNull();
    }

    @Test
    void dobleApertura_Returns400() {
        HttpEntity<String> request = new HttpEntity<>(
                "{\"montoInicial\":50000}", authHeaders);
        ResponseEntity<String> firstResponse = restTemplate.postForEntity("/api/caja/apertura", request, String.class);
        Number cajaId = JsonPath.read(firstResponse.getBody(), "$.data.id");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/caja/apertura", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Cleanup: close the caja so other tests are not affected
        HttpEntity<String> cierreReq = new HttpEntity<>(
                "{\"montoContado\":50000,\"observaciones\":\"Limpieza test\"}", authHeaders);
        restTemplate.exchange("/api/caja/" + cajaId.longValue() + "/cierre",
                HttpMethod.POST, cierreReq, String.class);
    }

    @Test
    void cerrarCaja_CalculaCorrectamente() {
        // Open caja
        HttpEntity<String> aperturaReq = new HttpEntity<>(
                "{\"montoInicial\":50000}", authHeaders);
        ResponseEntity<String> aperturaRes = restTemplate.postForEntity("/api/caja/apertura", aperturaReq, String.class);
        assertThat(JsonPath.<Boolean>read(aperturaRes.getBody(), "$.success")).isTrue();
        Number cajaIdNum = JsonPath.read(aperturaRes.getBody(), "$.data.id");
        Long cajaId = cajaIdNum.longValue();

        // Create an orden
        HttpEntity<String> createReq = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000," +
                "\"servicios\":[{\"servicioId\":1,\"cantidad\":1}]," +
                "\"productos\":[{\"productoId\":1,\"cantidad\":1}]}", authHeaders);
        ResponseEntity<String> createRes = restTemplate.postForEntity("/api/ordenes", createReq, String.class);
        Number ordenIdNum = JsonPath.read(createRes.getBody(), "$.data.id");
        Long ordenId = ordenIdNum.longValue();

        // Complete the orden through the full flow to ENTREGADO
        String[][] estados = {
            {"EN_DIAGNOSTICO", ""},
            {"EN_PROCESO", ""},
            {"LISTO_PARA_ENTREGA", ""},
            {"ENTREGADO", ""}
        };
        for (String[] e : estados) {
            HttpEntity<String> req = new HttpEntity<>(
                    "{\"estado\":\"" + e[0] + "\"}", authHeaders);
            ResponseEntity<String> res = restTemplate.exchange(
                    "/api/ordenes/" + ordenId + "/estado", HttpMethod.PATCH, req, String.class);
            assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Register a payment
        HttpEntity<String> pagoReq = new HttpEntity<>(
                "{\"ordenId\":" + ordenId + ",\"metodoPagoId\":1,\"valor\":50000}", authHeaders);
        ResponseEntity<String> pagoRes = restTemplate.postForEntity("/api/pagos", pagoReq, String.class);
        assertThat(pagoRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Close caja - montoContado should include montoInicial + payment
        HttpEntity<String> cierreReq = new HttpEntity<>(
                "{\"montoContado\":100500}", authHeaders);
        ResponseEntity<String> cierreRes = restTemplate.exchange(
                "/api/caja/" + cajaId + "/cierre", HttpMethod.POST, cierreReq, String.class);

        assertThat(cierreRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<String>read(cierreRes.getBody(), "$.data.estado")).isEqualTo("CERRADA");
        Number totalEsperado = JsonPath.read(cierreRes.getBody(), "$.data.totalEsperado");
        assertThat(totalEsperado.doubleValue()).isGreaterThan(50000);
        assertThat(JsonPath.<Object>read(cierreRes.getBody(), "$.data.fechaCierre")).isNotNull();
    }

    @Test
    void registrarPago_CambiaEstadoFinanciero() {
        HttpEntity<String> createReq = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000," +
                "\"servicios\":[{\"servicioId\":1,\"cantidad\":1}]," +
                "\"productos\":[{\"productoId\":1,\"cantidad\":1}]}", authHeaders);
        ResponseEntity<String> createRes = restTemplate.postForEntity("/api/ordenes", createReq, String.class);
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Number ordenIdNum = JsonPath.read(createRes.getBody(), "$.data.id");
        Long ordenId = ordenIdNum.longValue();
        Number totalGeneral = JsonPath.read(createRes.getBody(), "$.data.totalGeneral");

        assertThat(JsonPath.<String>read(createRes.getBody(), "$.data.estadoFinanciero")).isEqualTo("SIN_PAGAR");

        HttpEntity<String> pagoReq = new HttpEntity<>(
                "{\"ordenId\":" + ordenId + ",\"metodoPagoId\":1,\"valor\":" + totalGeneral + "}", authHeaders);
        ResponseEntity<String> pagoRes = restTemplate.postForEntity("/api/pagos", pagoReq, String.class);
        assertThat(pagoRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> ordenRes = restTemplate.exchange(
                "/api/ordenes/" + ordenId, HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);
        assertThat(JsonPath.<String>read(ordenRes.getBody(), "$.data.estadoFinanciero")).isEqualTo("PAGADA");
    }

    @Test
    void pagosParciales_ActualizaSaldo() {
        HttpEntity<String> createReq = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000," +
                "\"servicios\":[{\"servicioId\":1,\"cantidad\":1}]," +
                "\"productos\":[{\"productoId\":1,\"cantidad\":1}]}", authHeaders);
        ResponseEntity<String> createRes = restTemplate.postForEntity("/api/ordenes", createReq, String.class);
        Number ordenIdNum = JsonPath.read(createRes.getBody(), "$.data.id");
        Long ordenId = ordenIdNum.longValue();

        HttpEntity<String> pago1 = new HttpEntity<>(
                "{\"ordenId\":" + ordenId + ",\"metodoPagoId\":1,\"valor\":30000}", authHeaders);
        ResponseEntity<String> pago1Res = restTemplate.postForEntity("/api/pagos", pago1, String.class);
        assertThat(pago1Res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> ordenCheck1 = restTemplate.exchange(
                "/api/ordenes/" + ordenId, HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);
        assertThat(JsonPath.<String>read(ordenCheck1.getBody(), "$.data.estadoFinanciero")).isEqualTo("PARCIAL");

        HttpEntity<String> pago2 = new HttpEntity<>(
                "{\"ordenId\":" + ordenId + ",\"metodoPagoId\":1,\"valor\":30000}", authHeaders);
        ResponseEntity<String> pago2Res = restTemplate.postForEntity("/api/pagos", pago2, String.class);
        assertThat(pago2Res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> saldoRes = restTemplate.exchange(
                "/api/pagos/orden/" + ordenId + "/saldo", HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);
        double saldo = JsonPath.read(saldoRes.getBody(), "$.data");
        assertThat(saldo).isGreaterThan(0);

        Number totalGeneral = JsonPath.read(ordenCheck1.getBody(), "$.data.totalGeneral");
        double restante = totalGeneral.doubleValue() - 60000;
        if (restante > 0) {
            HttpEntity<String> pago3 = new HttpEntity<>(
                    "{\"ordenId\":" + ordenId + ",\"metodoPagoId\":1,\"valor\":" + (long) restante + "}", authHeaders);
            restTemplate.postForEntity("/api/pagos", pago3, String.class);
        }

        ResponseEntity<String> ordenCheck2 = restTemplate.exchange(
                "/api/ordenes/" + ordenId, HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);
        assertThat(JsonPath.<String>read(ordenCheck2.getBody(), "$.data.estadoFinanciero")).isEqualTo("PAGADA");
    }

    @Test
    void pagoExcedeSaldo_Returns400() {
        HttpEntity<String> createReq = new HttpEntity<>(
                "{\"clienteId\":1,\"vehiculoId\":1,\"kilometraje\":50000," +
                "\"servicios\":[{\"servicioId\":1,\"cantidad\":1}]}", authHeaders);
        ResponseEntity<String> createRes = restTemplate.postForEntity("/api/ordenes", createReq, String.class);
        Number ordenIdNum = JsonPath.read(createRes.getBody(), "$.data.id");
        Long ordenId = ordenIdNum.longValue();

        HttpEntity<String> pagoReq = new HttpEntity<>(
                "{\"ordenId\":" + ordenId + ",\"metodoPagoId\":1,\"valor\":9999999}", authHeaders);
        ResponseEntity<String> pagoRes = restTemplate.postForEntity("/api/pagos", pagoReq, String.class);
        assertThat(pagoRes.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void listarMetodosPago_ReturnsSeededData() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/metodos-pago", HttpMethod.GET, new HttpEntity<>(authHeaders), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(JsonPath.<Boolean>read(response.getBody(), "$.success")).isTrue();
    }
}

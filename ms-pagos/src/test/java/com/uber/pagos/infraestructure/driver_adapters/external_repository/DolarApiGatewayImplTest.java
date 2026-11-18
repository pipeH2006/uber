package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import com.uber.pagos.domain.exception.ErrorExternoException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DolarApiGatewayImplTest {

    private MockWebServer server;
    private DolarApiGatewayImpl gateway;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
        gateway = new DolarApiGatewayImpl(restClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void obtenerTipoCambio_respuestaValida_devuelveRate() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"success\",\"rates\":{\"USD\":1,\"COP\":4000.50}}"));

        BigDecimal rate = gateway.obtenerTipoCambio("COP");

        assertThat(rate).isEqualByComparingTo("4000.50");
    }

    @Test
    void obtenerTipoCambio_monedaMinuscula_normalizaAMayuscula() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4000.50}}"));

        BigDecimal rate = gateway.obtenerTipoCambio("cop");

        assertThat(rate).isEqualByComparingTo("4000.50");
    }

    @Test
    void obtenerTipoCambio_monedaInexistente_lanzaErrorExterno() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"USD\":1}}"));

        assertThatThrownBy(() -> gateway.obtenerTipoCambio("COP"))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void obtenerTipoCambio_respuestaSinRates_lanzaErrorExterno() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"success\"}"));

        assertThatThrownBy(() -> gateway.obtenerTipoCambio("COP"))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void obtenerTipoCambio_errorHttp_lanzaErrorExterno() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> gateway.obtenerTipoCambio("COP"))
                .isInstanceOf(ErrorExternoException.class);
    }
}

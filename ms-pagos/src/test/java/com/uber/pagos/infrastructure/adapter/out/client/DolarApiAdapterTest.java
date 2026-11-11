package com.uber.pagos.infrastructure.adapter.out.client;

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

class DolarApiAdapterTest {

    private MockWebServer server;
    private DolarApiAdapter adapter;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        RestClient restClient = RestClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
        adapter = new DolarApiAdapter(restClient);
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

        BigDecimal rate = adapter.obtenerTipoCambio("COP");

        assertThat(rate).isEqualByComparingTo("4000.50");
    }

    @Test
    void obtenerTipoCambio_monedaMinuscula_normalizaAMayuscula() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4000.50}}"));

        BigDecimal rate = adapter.obtenerTipoCambio("cop");

        assertThat(rate).isEqualByComparingTo("4000.50");
    }

    @Test
    void obtenerTipoCambio_monedaInexistente_lanzaErrorExterno() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"USD\":1}}"));

        assertThatThrownBy(() -> adapter.obtenerTipoCambio("COP"))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void obtenerTipoCambio_respuestaSinRates_lanzaErrorExterno() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"success\"}"));

        assertThatThrownBy(() -> adapter.obtenerTipoCambio("COP"))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void obtenerTipoCambio_errorHttp_lanzaErrorExterno() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> adapter.obtenerTipoCambio("COP"))
                .isInstanceOf(ErrorExternoException.class);
    }
}

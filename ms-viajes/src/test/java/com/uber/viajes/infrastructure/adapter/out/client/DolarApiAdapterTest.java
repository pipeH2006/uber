package com.uber.viajes.infrastructure.adapter.out.client;

import com.uber.viajes.domain.exception.ErrorExternoException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DolarApiAdapterTest {

    private MockWebServer mockWebServer;
    private DolarApiAdapter adapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        adapter = new DolarApiAdapter(restClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void obtenerTipoCambio_respuestaValida_devuelveRate() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"result\":\"success\",\"rates\":{\"COP\":4000.50}}"));

        BigDecimal rate = adapter.obtenerTipoCambio("COP");

        assertThat(rate).isEqualByComparingTo("4000.50");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).startsWith("/v6/latest/USD");
    }

    @Test
    void obtenerTipoCambio_convierteMonedaAMayusculas() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4000.50}}"));

        BigDecimal rate = adapter.obtenerTipoCambio("cop");

        assertThat(rate).isEqualByComparingTo("4000.50");
    }

    @Test
    void obtenerTipoCambio_monedaSinRate_lanzaErrorExterno() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{}}"));

        assertThatThrownBy(() -> adapter.obtenerTipoCambio("EUR"))
                .isInstanceOf(ErrorExternoException.class);
    }
}

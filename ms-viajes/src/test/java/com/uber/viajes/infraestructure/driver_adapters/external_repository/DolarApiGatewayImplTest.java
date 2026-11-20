package com.uber.viajes.infraestructure.driver_adapters.external_repository;

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
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DolarApiGatewayImplTest {

    private MockWebServer mockWebServer;
    private DolarApiGatewayImpl gateway;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        RestClient restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        gateway = new DolarApiGatewayImpl(restClient);
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

        BigDecimal rate = gateway.obtenerTipoCambio("COP");

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

        BigDecimal rate = gateway.obtenerTipoCambio("cop");

        assertThat(rate).isEqualByComparingTo("4000.50");
    }

    @Test
    void obtenerTipoCambio_monedaSinRate_lanzaErrorExterno() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{}}"));

        assertThatThrownBy(() -> gateway.obtenerTipoCambio("EUR"))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void obtenerTipoCambio_segundaConsultaDentroDelTtl_usaCache() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4000.50}}"));
        RelojMutable reloj = new RelojMutable();
        DolarApiGatewayImpl conCache = new DolarApiGatewayImpl(clienteActual(), reloj);

        BigDecimal primera = conCache.obtenerTipoCambio("COP");
        reloj.avanzar(DolarApiGatewayImpl.TTL.minusSeconds(1));
        BigDecimal segunda = conCache.obtenerTipoCambio("cop");

        assertThat(primera).isEqualByComparingTo("4000.50");
        assertThat(segunda).isEqualByComparingTo("4000.50");
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void obtenerTipoCambio_trasExpirarElTtl_consultaDeNuevoAlProveedor() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4000.50}}"));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4100.25}}"));
        RelojMutable reloj = new RelojMutable();
        DolarApiGatewayImpl conCache = new DolarApiGatewayImpl(clienteActual(), reloj);

        conCache.obtenerTipoCambio("COP");
        reloj.avanzar(DolarApiGatewayImpl.TTL.plusSeconds(1));
        BigDecimal renovada = conCache.obtenerTipoCambio("COP");

        assertThat(renovada).isEqualByComparingTo("4100.25");
        assertThat(mockWebServer.getRequestCount()).isEqualTo(2);
    }

    @Test
    void obtenerTipoCambio_errorDelProveedor_noSeGuardaEnCache() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"rates\":{\"COP\":4000.50}}"));
        DolarApiGatewayImpl conCache = new DolarApiGatewayImpl(clienteActual(), new RelojMutable());

        assertThatThrownBy(() -> conCache.obtenerTipoCambio("COP")).isInstanceOf(RuntimeException.class);
        assertThat(conCache.obtenerTipoCambio("COP")).isEqualByComparingTo("4000.50");
    }

    private RestClient clienteActual() {
        return RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
    }

    private static final class RelojMutable extends Clock {

        private Instant ahora = Instant.parse("2026-01-01T00:00:00Z");

        void avanzar(Duration duracion) {
            ahora = ahora.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return ahora;
        }
    }
}

package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacionRestGatewayAsyncTest {
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void notificar_noEsperaLaRespuestaDeMsNotificaciones() throws Exception {
        String url = mockWebServer.url("/").toString();
        NotificacionRestGatewayImpl gateway = new NotificacionRestGatewayImpl(RestClient.builder().baseUrl(url).build(), url);
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBodyDelay(1500, TimeUnit.MILLISECONDS));

        long inicio = System.nanoTime();
        gateway.notificar("555", "Hola");
        Duration tardanza = Duration.ofNanos(System.nanoTime() - inicio);

        assertThat(tardanza).isLessThan(Duration.ofMillis(800));
        RecordedRequest request = mockWebServer.takeRequest(5, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getBody().readUtf8()).contains("\"destinatario\":\"555\"");
    }
}

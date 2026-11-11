package com.uber.viajes.infrastructure.adapter.out.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class NotificacionRestAdapterTest {

    private MockWebServer mockWebServer;
    private RestClient restClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void notificar_respuesta200_noLanzaExcepcion() throws Exception {
        NotificacionRestAdapter adapter = new NotificacionRestAdapter(
                restClient, mockWebServer.url("/").toString());
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        assertThatCode(() -> adapter.notificar("conductor-2", "Nuevo viaje")).doesNotThrowAnyException();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).startsWith("/api/v1/notificaciones/enviar");
    }

    @Test
    void notificar_errorServidor_noLanzaExcepcion() {
        NotificacionRestAdapter adapter = new NotificacionRestAdapter(
                restClient, mockWebServer.url("/").toString());
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatCode(() -> adapter.notificar("conductor-2", "Nuevo viaje")).doesNotThrowAnyException();
    }

    @Test
    void notificar_urlVacia_noRealizaLlamada() {
        NotificacionRestAdapter adapter = new NotificacionRestAdapter(restClient, "");

        assertThatCode(() -> adapter.notificar("conductor-2", "Nuevo viaje")).doesNotThrowAnyException();
        assertThat(mockWebServer.getRequestCount()).isZero();
    }

    @Test
    void notificar_urlNula_noRealizaLlamada() {
        NotificacionRestAdapter adapter = new NotificacionRestAdapter(restClient, null);

        assertThatCode(() -> adapter.notificar("conductor-2", "Nuevo viaje")).doesNotThrowAnyException();
        assertThat(mockWebServer.getRequestCount()).isZero();
    }
}

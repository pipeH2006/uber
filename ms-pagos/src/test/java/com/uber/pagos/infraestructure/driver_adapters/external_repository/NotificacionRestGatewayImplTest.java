package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class NotificacionRestGatewayImplTest {

    private MockWebServer server;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void notificar_urlEnBlanco_noEnviaNiLanza() {
        NotificacionRestGatewayImpl gateway = new NotificacionRestGatewayImpl(RestClient.create(), "");

        assertThatCode(() -> gateway.notificar("usuario-1", "mensaje"))
                .doesNotThrowAnyException();
    }

    @Test
    void notificar_respuesta200_enviaSinLanzar() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200));
        String url = server.url("/").toString();
        RestClient restClient = RestClient.builder().baseUrl(url).build();
        NotificacionRestGatewayImpl gateway = new NotificacionRestGatewayImpl(restClient, url);

        gateway.notificar("usuario-1", "Pago aprobado");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).isEqualTo("/api/v1/notificaciones/enviar");
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getBody().readUtf8())
                .contains("\"destinatario\":\"usuario-1\"")
                .contains("\"mensaje\":\"Pago aprobado\"");
    }

    @Test
    void notificar_errorNoLanza() {
        server.enqueue(new MockResponse().setResponseCode(500));
        String url = server.url("/").toString();
        RestClient restClient = RestClient.builder().baseUrl(url).build();
        NotificacionRestGatewayImpl gateway = new NotificacionRestGatewayImpl(restClient, url);

        assertThatCode(() -> gateway.notificar("usuario-1", "mensaje"))
                .doesNotThrowAnyException();
    }
}

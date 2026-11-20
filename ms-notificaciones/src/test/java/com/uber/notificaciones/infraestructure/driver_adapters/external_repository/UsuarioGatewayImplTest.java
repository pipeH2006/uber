package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioGatewayImplTest {

    private MockWebServer server;
    private UsuarioGatewayImpl gateway;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        String url = server.url("/").toString();
        gateway = new UsuarioGatewayImpl(RestClient.builder().baseUrl(url).build(), url);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void vincularTelegram_usuarioExiste_haceElPutYDevuelveTrue() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody("{}"));

        assertThat(gateway.vincularTelegram(7L, "555")).isTrue();

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo("PUT");
        assertThat(request.getPath()).isEqualTo("/api/v1/usuarios/7");
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"telegramChatId\":\"555\"}");
    }

    @Test
    void vincularTelegram_usuarioNoExiste_devuelveFalse() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThat(gateway.vincularTelegram(99L, "555")).isFalse();
    }

    @Test
    void vincularTelegram_errorServidor_propagaLaExcepcion() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> gateway.vincularTelegram(7L, "555")).isInstanceOf(RestClientException.class);
    }

    @Test
    void vincularTelegram_urlSinConfigurar_lanzaIllegalState() {
        assertThatThrownBy(() -> new UsuarioGatewayImpl(RestClient.builder().build(), "").vincularTelegram(7L, "555"))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new UsuarioGatewayImpl(RestClient.builder().build(), null).vincularTelegram(7L, "555"))
                .isInstanceOf(IllegalStateException.class);
    }
}

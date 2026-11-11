package com.uber.notificaciones.infrastructure.adapter.out.client;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.infrastructure.config.ExternalProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelegramRestAdapterTest {

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

    private TelegramRestAdapter adapter(String token) {
        ExternalProperties props = new ExternalProperties();
        props.setTelegramBotToken(token);
        props.setTelegramBaseUrl(server.url("/").toString());
        RestClient client = RestClient.builder().baseUrl(server.url("/").toString()).build();
        return new TelegramRestAdapter(client, props);
    }

    @Test
    void enviar_respuesta200_noLanzaExcepcion() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));

        adapter("token-secreto").enviar("123456", "hola");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).isEqualTo("/bottoken-secreto/sendMessage");
        assertThat(request.getBody().readUtf8()).contains("\"chat_id\":\"123456\"");
    }

    @Test
    void enviar_respuesta500_lanzaEnvioNotificacionException() {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("{\"ok\":false}"));

        assertThatThrownBy(() -> adapter("token-secreto").enviar("123456", "hola"))
                .isInstanceOf(EnvioNotificacionException.class);
    }

    @Test
    void enviar_tokenVacio_simulaSinLlamarATelegram() {
        adapter("").enviar("123456", "hola");

        assertThat(server.getRequestCount()).isZero();
    }
}

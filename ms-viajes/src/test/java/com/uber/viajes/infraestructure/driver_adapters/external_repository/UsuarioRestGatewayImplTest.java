package com.uber.viajes.infraestructure.driver_adapters.external_repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioRestGatewayImplTest {

    private MockWebServer mockWebServer;
    private UsuarioRestGatewayImpl gateway;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String url = mockWebServer.url("/").toString();
        gateway = new UsuarioRestGatewayImpl(RestClient.builder().baseUrl(url).build(), url);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private static MockResponse json(String body) {
        return new MockResponse().setHeader("Content-Type", "application/json").setBody(body);
    }

    @Test
    void obtenerChatIdTelegram_usuarioConChat_devuelveChatId() throws Exception {
        mockWebServer.enqueue(json("{\"id\":7,\"telegramChatId\":\"123456789\"}"));

        assertThat(gateway.obtenerChatIdTelegram(7L)).contains("123456789");

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/api/v1/usuarios/7");
    }

    @Test
    void obtenerChatIdTelegram_usuarioSinChat_devuelveVacio() {
        mockWebServer.enqueue(json("{\"id\":7,\"telegramChatId\":null}"));

        assertThat(gateway.obtenerChatIdTelegram(7L)).isEmpty();
    }

    @Test
    void obtenerChatIdTelegram_cuerpoVacio_devuelveVacio() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200));

        assertThat(gateway.obtenerChatIdTelegram(7L)).isEmpty();
    }

    @Test
    void obtenerChatIdTelegram_usuarioNoExiste_devuelveVacio() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        assertThat(gateway.obtenerChatIdTelegram(99L)).isEmpty();
    }

    @Test
    void obtenerChatIdTelegram_errorServidor_devuelveVacio() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThat(gateway.obtenerChatIdTelegram(7L)).isEmpty();
    }

    @Test
    void obtenerChatIdTelegram_urlVacia_noRealizaLlamada() {
        UsuarioRestGatewayImpl sinUrl = new UsuarioRestGatewayImpl(RestClient.builder().build(), "");

        assertThat(sinUrl.obtenerChatIdTelegram(7L)).isEmpty();
        assertThat(mockWebServer.getRequestCount()).isZero();
    }

    @Test
    void obtenerChatIdTelegram_urlNula_noRealizaLlamada() {
        UsuarioRestGatewayImpl sinUrl = new UsuarioRestGatewayImpl(RestClient.builder().build(), null);

        assertThat(sinUrl.obtenerChatIdTelegram(7L)).isEmpty();
        assertThat(mockWebServer.getRequestCount()).isZero();
    }
}

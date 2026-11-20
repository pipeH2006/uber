package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class GroqRedactorGatewayImplTest {

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

    private GroqRedactorGatewayImpl adapter(String apiKey) {
        return new GroqRedactorGatewayImpl(RestClient.builder().baseUrl(server.url("/").toString()).build(), apiKey);
    }

    private static MockResponse json(String body) {
        return new MockResponse().setHeader("Content-Type", "application/json").setBody(body);
    }

    @Test
    void redactar_respuestaValida_devuelveElTextoDeLaIa() throws Exception {
        server.enqueue(json("{\"choices\":[{\"message\":{\"content\":\"  🚗 ¡Tu viaje comenzó!  \"}}]}"));

        String texto = adapter("clave").redactar("Tu viaje ha iniciado");

        assertThat(texto).isEqualTo("🚗 ¡Tu viaje comenzó!");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).isEqualTo("/v1/chat/completions");
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer clave");
        assertThat(request.getBody().readUtf8()).contains("Tu viaje ha iniciado");
    }

    @Test
    void redactar_respuestaMuyLarga_laRecorta() {
        server.enqueue(json("{\"choices\":[{\"message\":{\"content\":\"" + "a".repeat(900) + "\"}}]}"));

        assertThat(adapter("clave").redactar("base")).hasSize(600);
    }

    @Test
    void redactar_contenidoVacio_devuelveElMensajeOriginal() {
        server.enqueue(json("{\"choices\":[{\"message\":{\"content\":\"\"}}]}"));

        assertThat(adapter("clave").redactar("base")).isEqualTo("base");
    }

    @Test
    void redactar_sinOpciones_devuelveElMensajeOriginal() {
        server.enqueue(json("{\"choices\":[]}"));

        assertThat(adapter("clave").redactar("base")).isEqualTo("base");
    }

    @Test
    void redactar_cuerpoVacio_devuelveElMensajeOriginal() {
        server.enqueue(new MockResponse().setResponseCode(200));

        assertThat(adapter("clave").redactar("base")).isEqualTo("base");
    }

    @Test
    void redactar_errorDelProveedor_devuelveElMensajeOriginal() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThat(adapter("clave").redactar("base")).isEqualTo("base");
    }

    @Test
    void redactar_sinApiKey_noLlamaAGroq() {
        assertThat(adapter("").redactar("base")).isEqualTo("base");
        assertThat(adapter(null).redactar("base")).isEqualTo("base");
        assertThat(server.getRequestCount()).isZero();
    }
}

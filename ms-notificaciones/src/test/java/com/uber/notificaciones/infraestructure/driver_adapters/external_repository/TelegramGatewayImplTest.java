package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import com.uber.notificaciones.application.config.ExternalProperties;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.ActualizacionTelegram;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TelegramGatewayImplTest {
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

    private TelegramGatewayImpl adapter(String token) {
        ExternalProperties props = new ExternalProperties();
        props.setTelegramBotToken(token);
        props.setTelegramBaseUrl(server.url("/").toString());
        RestClient client = RestClient.builder().baseUrl(server.url("/").toString()).build();
        return new TelegramGatewayImpl(client, props);
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

    @Test
    void obtenerActualizaciones_leeLosMensajesDeTexto() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(
                "{\"ok\":true,\"result\":["
                        + "{\"update_id\":10,\"message\":{\"chat\":{\"id\":555},\"text\":\"/start 7\"}},"
                        + "{\"update_id\":11,\"message\":{\"chat\":{\"id\":666}}},"
                        + "{\"update_id\":12,\"edited_message\":{}},"
                        + "{\"update_id\":13,\"message\":{\"chat\":{\"id\":777},\"from\":{\"id\":777},"
                        + "\"contact\":{\"phone_number\":\"573001234567\",\"user_id\":777}}},"
                        + "{\"update_id\":14,\"message\":{\"chat\":{\"id\":888},\"from\":{\"id\":888},"
                        + "\"contact\":{\"phone_number\":\"573119876543\",\"user_id\":999}}}]}"));

        List<ActualizacionTelegram> resultado = adapter("token-secreto").obtenerActualizaciones(10);

        assertThat(resultado).containsExactly(
                new ActualizacionTelegram(10, "555", "/start 7"),
                new ActualizacionTelegram(11, "666", null),
                new ActualizacionTelegram(12, null, null),
                new ActualizacionTelegram(13, "777", null, "573001234567", true),
                new ActualizacionTelegram(14, "888", null, "573119876543", false));
        assertThat(server.takeRequest().getPath()).isEqualTo("/bottoken-secreto/getUpdates?offset=10&timeout=0");
    }

    @Test
    void obtenerActualizaciones_cuerpoVacio_devuelveListaVacia() {
        server.enqueue(new MockResponse().setResponseCode(200));

        assertThat(adapter("token-secreto").obtenerActualizaciones(0)).isEmpty();
    }

    @Test
    void obtenerActualizaciones_errorDeTelegram_devuelveListaVacia() {
        server.enqueue(new MockResponse().setResponseCode(409).setBody("{\"ok\":false}"));

        assertThat(adapter("token-secreto").obtenerActualizaciones(0)).isEmpty();
    }

    @Test
    void obtenerActualizaciones_tokenVacio_noLlamaATelegram() {
        assertThat(adapter("").obtenerActualizaciones(0)).isEmpty();
        assertThat(server.getRequestCount()).isZero();
    }

    @Test
    void solicitarContacto_enviaElTecladoConBotonDeContacto() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{\"ok\":true}"));

        adapter("token-secreto").solicitarContacto("123456", "Comparte tu número");

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).isEqualTo("/bottoken-secreto/sendMessage");
        String cuerpo = request.getBody().readUtf8();
        assertThat(cuerpo).contains("\"request_contact\":true").contains("\"one_time_keyboard\":true");
    }

    @Test
    void solicitarContacto_tokenVacio_simulaSinLlamarATelegram() {
        adapter("").solicitarContacto("123456", "hola");

        assertThat(server.getRequestCount()).isZero();
    }
}

package com.uber.pagos.infrastructure.adapter.out.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenAiFraudeAdapterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockWebServer server;
    private RestClient restClient;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        restClient = RestClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    private Pago pago() {
        return Pago.builder()
                .id(1L)
                .viajeId(10L)
                .usuarioId(2L)
                .monto(new BigDecimal("100.00"))
                .moneda("USD")
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.APROBADO)
                .referencia("PAG-123")
                .build();
    }

    @Test
    void evaluar_apiKeyEnBlanco_devuelveSimulacion() {
        OpenAiFraudeAdapter adapter = new OpenAiFraudeAdapter(restClient, "", objectMapper);

        ResultadoFraude resultado = adapter.evaluar(pago());

        assertThat(resultado.riesgo()).isEqualTo("BAJO");
        assertThat(resultado.score()).isEqualTo(10);
        assertThat(resultado.justificacion()).contains("simulación");
    }

    @Test
    void evaluar_apiKeyNula_devuelveSimulacion() {
        OpenAiFraudeAdapter adapter = new OpenAiFraudeAdapter(restClient, null, objectMapper);

        ResultadoFraude resultado = adapter.evaluar(pago());

        assertThat(resultado.riesgo()).isEqualTo("BAJO");
    }

    @Test
    void evaluar_respuestaValida_devuelveResultado() throws Exception {
        String inner = objectMapper.writeValueAsString(new ResultadoFraude("ALTO", 85, "Monto atipico"));
        String body = "{\"choices\":[{\"message\":{\"content\":" + objectMapper.writeValueAsString(inner) + "}}]}";
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(body));
        OpenAiFraudeAdapter adapter = new OpenAiFraudeAdapter(restClient, "sk-test", objectMapper);

        ResultadoFraude resultado = adapter.evaluar(pago());

        assertThat(resultado.riesgo()).isEqualTo("ALTO");
        assertThat(resultado.score()).isEqualTo(85);
        assertThat(resultado.justificacion()).isEqualTo("Monto atipico");
    }

    @Test
    void evaluar_respuestaSinChoices_lanzaErrorExterno() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"choices\":[]}"));
        OpenAiFraudeAdapter adapter = new OpenAiFraudeAdapter(restClient, "sk-test", objectMapper);

        assertThatThrownBy(() -> adapter.evaluar(pago()))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void evaluar_errorHttp_lanzaErrorExterno() {
        server.enqueue(new MockResponse().setResponseCode(500));
        OpenAiFraudeAdapter adapter = new OpenAiFraudeAdapter(restClient, "sk-test", objectMapper);

        assertThatThrownBy(() -> adapter.evaluar(pago()))
                .isInstanceOf(ErrorExternoException.class);
    }
}

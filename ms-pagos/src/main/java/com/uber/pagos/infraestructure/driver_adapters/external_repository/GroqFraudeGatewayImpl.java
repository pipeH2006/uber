package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import com.uber.pagos.domain.model.gateway.FraudeGateway;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Adaptador del gateway de fraude usando Groq (inferencia de modelos
 * open source tipo Llama, con API compatible con OpenAI y plan gratuito).
 */
public class GroqFraudeGatewayImpl implements FraudeGateway {

    private static final String MODEL = "openai/gpt-oss-120b";
    private static final String SYSTEM_PROMPT =
            "Eres un analista de riesgo de fraude para pagos de una plataforma de transporte. "
            + "Analiza los datos del pago y responde ÚNICAMENTE con un objeto JSON que contenga los campos: "
            + "riesgo (\"BAJO\", \"MEDIO\" o \"ALTO\"), score (entero entre 0 y 100) y justificacion (texto breve). "
            + "No incluyas texto adicional ni formato markdown.";

    private final RestClient restClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public GroqFraudeGatewayImpl(RestClient restClient, String apiKey, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResultadoFraude evaluar(Pago pago) {
        if (apiKey == null || apiKey.isBlank()) {
            return new ResultadoFraude("BAJO", 10, "Modo simulación: no se realizó análisis de IA");
        }
        try {
            String responseBody = restClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildBody(pago))
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            if (choices.isEmpty()) {
                throw new ErrorExternoException("La respuesta del servicio de IA no contiene opciones");
            }
            String content = choices.get(0).path("message").path("content").asText();
            ResultadoFraude resultado = objectMapper.readValue(content, ResultadoFraude.class);
            return new ResultadoFraude(
                    resultado.riesgo().toUpperCase(),
                    resultado.score(),
                    resultado.justificacion());
        } catch (ErrorExternoException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorExternoException("Error al evaluar fraude: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildBody(Pago pago) {
        String userContent = "Analiza el siguiente pago: viajeId=" + pago.getViajeId()
                + ", usuarioId=" + pago.getUsuarioId()
                + ", monto=" + pago.getMonto() + " " + pago.getMoneda()
                + ", metodoPago=" + pago.getMetodoPago()
                + ", fecha=" + pago.getFecha()
                + ", referencia=" + pago.getReferencia();

        return Map.of(
                "model", MODEL,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userContent)
                )
        );
    }
}

package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.notificaciones.domain.model.gateway.RedactorGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
public class GroqRedactorGatewayImpl implements RedactorGateway {
    private static final String MODEL = "openai/gpt-oss-120b";
    private static final int MAX_CARACTERES = 600;
    private static final String SYSTEM_PROMPT =
            "Eres el asistente de notificaciones de una app de transporte privado tipo Uber. "
            + "Reescribe el mensaje que te den en español, con tono cálido, claro y breve (máximo 2 frases) "
            + "y como máximo un emoji. No inventes información ni cambies cifras, nombres, estados o referencias. "
            + "Responde únicamente con el texto final del mensaje, sin comillas ni explicaciones.";

    private final RestClient restClient;
    private final String apiKey;

    public GroqRedactorGatewayImpl(RestClient restClient, String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    @Override
    public String redactar(String mensajeBase) {
        if (apiKey == null || apiKey.isBlank()) {
            return mensajeBase;
        }
        try {
            JsonNode root = restClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "model", MODEL,
                            "messages", List.of(
                                    Map.of("role", "system", "content", SYSTEM_PROMPT),
                                    Map.of("role", "user", "content", mensajeBase))))
                    .retrieve()
                    .body(JsonNode.class);
            String texto = root == null ? "" : root.path("choices").path(0).path("message").path("content").asText("").trim();
            if (texto.isBlank()) {
                return mensajeBase;
            }
            return texto.length() > MAX_CARACTERES ? texto.substring(0, MAX_CARACTERES) : texto;
        } catch (Exception e) {
            log.warn("No se pudo redactar el mensaje con IA; se envía el texto original: {}", e.getMessage());
            return mensajeBase;
        }
    }
}

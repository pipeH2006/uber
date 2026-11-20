package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import com.uber.notificaciones.application.config.ExternalProperties;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import com.fasterxml.jackson.databind.JsonNode;
import com.uber.notificaciones.domain.model.ActualizacionTelegram;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class TelegramGatewayImpl implements TelegramGateway {
    private final RestClient telegramRestClient;
    private final ExternalProperties properties;

    @Override
    public void enviar(String chatId, String texto) {
        enviarMensaje(Map.of("chat_id", chatId, "text", texto));
    }

    @Override
    public void solicitarContacto(String chatId, String texto) {
        Map<String, Object> botonContacto = Map.of("text", "📱 Compartir mi número", "request_contact", true);
        enviarMensaje(Map.of(
                "chat_id", chatId,
                "text", texto,
                "reply_markup", Map.of(
                        "keyboard", List.of(List.of(botonContacto)),
                        "resize_keyboard", true,
                        "one_time_keyboard", true)));
    }

    private void enviarMensaje(Map<String, Object> cuerpo) {
        String token = properties.getTelegramBotToken();
        if (token == null || token.isBlank()) {
            log.info("MODO SIMULACIÓN: notificación no enviada realmente a Telegram (botToken vacío)");
            return;
        }
        try {
            telegramRestClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/bot{token}/sendMessage").build(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new EnvioNotificacionException(
                    "Error al enviar mensaje a Telegram (HTTP " + e.getStatusCode().value() + ")");
        }
    }

    @Override
    public List<ActualizacionTelegram> obtenerActualizaciones(long offset) {
        String token = properties.getTelegramBotToken();
        if (token == null || token.isBlank()) {
            return List.of();
        }
        try {
            JsonNode root = telegramRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/bot{token}/getUpdates")
                            .queryParam("offset", offset)
                            .queryParam("timeout", 0)
                            .build(token))
                    .retrieve()
                    .body(JsonNode.class);
            List<ActualizacionTelegram> actualizaciones = new ArrayList<>();
            if (root != null) {
                for (JsonNode update : root.path("result")) {
                    JsonNode mensaje = update.path("message");
                    JsonNode chat = mensaje.path("chat");
                    JsonNode contacto = mensaje.path("contact");
                    boolean contactoPropio = contacto.hasNonNull("user_id") && mensaje.path("from").hasNonNull("id")
                            && contacto.get("user_id").asLong() == mensaje.path("from").get("id").asLong();
                    actualizaciones.add(new ActualizacionTelegram(
                            update.path("update_id").asLong(),
                            chat.hasNonNull("id") ? chat.get("id").asText() : null,
                            mensaje.hasNonNull("text") ? mensaje.get("text").asText() : null,
                            contacto.hasNonNull("phone_number") ? contacto.get("phone_number").asText() : null,
                            contactoPropio));
                }
            }
            return actualizaciones;
        } catch (RestClientResponseException e) {
            log.warn("No se pudieron leer los mensajes del bot (HTTP {}); ¿hay otra instancia consultando el mismo bot?",
                    e.getStatusCode().value());
            return List.of();
        }
    }
}

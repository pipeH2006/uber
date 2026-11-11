package com.uber.notificaciones.infrastructure.adapter.out.client;

import com.uber.notificaciones.application.port.out.TelegramPort;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.infrastructure.config.ExternalProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

/**
 * Adaptador del puerto de salida {@link TelegramPort} que invoca la API de
 * Telegram mediante {@link RestClient}. Se registra como bean en
 * {@code RestClientConfig}.
 */
@Slf4j
@RequiredArgsConstructor
public class TelegramRestAdapter implements TelegramPort {

    private final RestClient telegramRestClient;
    private final ExternalProperties properties;

    @Override
    public void enviar(String chatId, String texto) {
        String token = properties.getTelegramBotToken();
        if (token == null || token.isBlank()) {
            log.info("MODO SIMULACIÓN: notificación no enviada realmente a Telegram (botToken vacío)");
            return;
        }
        try {
            telegramRestClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/bot{token}/sendMessage").build(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("chat_id", chatId, "text", texto))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw new EnvioNotificacionException(
                    "Error al enviar mensaje a Telegram (HTTP " + e.getStatusCode().value() + ")");
        }
    }
}

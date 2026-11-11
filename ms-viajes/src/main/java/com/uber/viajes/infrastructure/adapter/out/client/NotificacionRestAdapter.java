package com.uber.viajes.infrastructure.adapter.out.client;

import com.uber.viajes.application.port.out.NotificacionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
public class NotificacionRestAdapter implements NotificacionPort {

    private final RestClient restClient;
    private final String notificacionesUrl;

    public NotificacionRestAdapter(RestClient restClient, String notificacionesUrl) {
        this.restClient = restClient;
        this.notificacionesUrl = notificacionesUrl;
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        if (notificacionesUrl == null || notificacionesUrl.isBlank()) {
            log.warn("URL de notificaciones no configurada; se omite el envío");
            return;
        }
        try {
            restClient.post()
                    .uri("api/v1/notificaciones/enviar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("destinatario", destinatario, "mensaje", mensaje))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo enviar la notificación a {}: {}", destinatario, e.getMessage());
        }
    }
}

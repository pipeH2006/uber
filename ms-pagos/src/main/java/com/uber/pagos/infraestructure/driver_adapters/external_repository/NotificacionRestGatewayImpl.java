package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import com.uber.pagos.domain.model.gateway.NotificacionGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
public class NotificacionRestGatewayImpl implements NotificacionGateway {

    private final RestClient restClient;
    private final String notificacionesUrl;

    public NotificacionRestGatewayImpl(RestClient restClient, String notificacionesUrl) {
        this.restClient = restClient;
        this.notificacionesUrl = notificacionesUrl;
    }

    @Override
    public void notificar(String destinatario, String mensaje) {
        if (notificacionesUrl == null || notificacionesUrl.isBlank()) {
            log.info("Notificación omitida: URL de notificaciones no configurada");
            return;
        }
        try {
            restClient.post()
                    .uri("/api/v1/notificaciones/enviar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("destinatario", destinatario, "mensaje", mensaje))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Error al enviar notificación (best-effort): {}", e.getMessage());
        }
    }
}

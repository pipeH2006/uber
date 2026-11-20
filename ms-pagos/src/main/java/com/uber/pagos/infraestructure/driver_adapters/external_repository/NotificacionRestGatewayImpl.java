package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import com.uber.pagos.domain.model.gateway.NotificacionGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class NotificacionRestGatewayImpl implements NotificacionGateway {
    private final RestClient restClient;
    private final String notificacionesUrl;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

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
        executor.execute(() -> enviar(destinatario, mensaje));
    }

    private void enviar(String destinatario, String mensaje) {
        try {
            restClient.post()
                    .uri("/api/v1/notificaciones/enviar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("destinatario", destinatario, "mensaje", mensaje))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("No se pudo enviar la notificación a {}: {}", destinatario, e.getMessage());
        }
    }
}

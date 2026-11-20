package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;

public class UsuarioGatewayImpl implements UsuarioGateway {

    private final RestClient restClient;
    private final String usuariosUrl;

    public UsuarioGatewayImpl(RestClient restClient, String usuariosUrl) {
        this.restClient = restClient;
        this.usuariosUrl = usuariosUrl;
    }

    @Override
    public boolean vincularTelegram(Long usuarioId, String chatId) {
        if (usuariosUrl == null || usuariosUrl.isBlank()) {
            throw new IllegalStateException("USUARIOS_URL no está configurada");
        }
        try {
            restClient.put()
                    .uri("/api/v1/usuarios/{id}", usuarioId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("telegramChatId", chatId))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}

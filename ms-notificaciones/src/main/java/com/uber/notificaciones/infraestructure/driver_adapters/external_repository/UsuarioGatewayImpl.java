package com.uber.notificaciones.infraestructure.driver_adapters.external_repository;

import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Override
    public int vincularPorTelefono(String telefono, String chatId) {
        if (usuariosUrl == null || usuariosUrl.isBlank()) {
            throw new IllegalStateException("USUARIOS_URL no está configurada");
        }
        JsonNode usuarios = restClient.get()
                .uri("/api/v1/usuarios/buscar-por-telefono?telefono={telefono}", telefono)
                .retrieve()
                .body(JsonNode.class);
        int vinculados = 0;
        if (usuarios != null) {
            for (JsonNode usuario : usuarios) {
                if (vincularTelegram(usuario.path("id").asLong(), chatId)) {
                    vinculados++;
                }
            }
        }
        return vinculados;
    }
}

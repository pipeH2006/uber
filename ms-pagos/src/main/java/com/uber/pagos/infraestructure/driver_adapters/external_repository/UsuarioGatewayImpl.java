package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.model.gateway.UsuarioGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final RestClient restClient;
    private final String usuariosUrl;

    public UsuarioGatewayImpl(RestClient restClient, String usuariosUrl) {
        this.restClient = restClient;
        this.usuariosUrl = usuariosUrl;
    }

    @Override
    public boolean usuarioExiste(Long usuarioId) {
        if (urlNoConfigurada()) {
            log.warn("URL de usuarios no configurada; se omite la validacion del usuario {}", usuarioId);
            return true;
        }
        try {
            restClient.get()
                    .uri("/api/v1/usuarios/{id}", usuarioId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new ErrorExternoException("Error al consultar el servicio de usuarios: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<String> obtenerChatIdTelegram(Long usuarioId) {
        if (urlNoConfigurada()) {
            log.warn("URL de usuarios no configurada; no se puede resolver el chat de Telegram del usuario {}", usuarioId);
            return Optional.empty();
        }
        try {
            JsonNode usuario = restClient.get()
                    .uri("/api/v1/usuarios/{id}", usuarioId)
                    .retrieve()
                    .body(JsonNode.class);
            String chatId = usuario == null ? "" : usuario.path("telegramChatId").asText("");
            if (chatId.isBlank()) {
                log.warn("El usuario {} no tiene chat de Telegram registrado; se omite la notificacion", usuarioId);
                return Optional.empty();
            }
            return Optional.of(chatId);
        } catch (Exception e) {
            log.warn("No se pudo consultar el usuario {} en ms-usuarios: {}", usuarioId, e.getMessage());
            return Optional.empty();
        }
    }

    private boolean urlNoConfigurada() {
        return usuariosUrl == null || usuariosUrl.isBlank();
    }
}

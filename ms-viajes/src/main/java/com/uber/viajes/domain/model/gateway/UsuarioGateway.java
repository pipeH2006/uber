package com.uber.viajes.domain.model.gateway;

import java.util.Optional;

public interface UsuarioGateway {

    /**
     * Devuelve el chat ID de Telegram del usuario, o vacio si no existe,
     * no lo tiene registrado o ms-usuarios no esta disponible.
     */
    Optional<String> obtenerChatIdTelegram(Long usuarioId);
}

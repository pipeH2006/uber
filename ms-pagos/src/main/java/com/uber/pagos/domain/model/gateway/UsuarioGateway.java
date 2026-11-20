package com.uber.pagos.domain.model.gateway;

import java.util.Optional;

public interface UsuarioGateway {

    boolean usuarioExiste(Long usuarioId);

    /**
     * Devuelve el chat ID de Telegram del usuario, o vacio si no lo tiene registrado
     * o ms-usuarios no esta disponible.
     */
    Optional<String> obtenerChatIdTelegram(Long usuarioId);
}

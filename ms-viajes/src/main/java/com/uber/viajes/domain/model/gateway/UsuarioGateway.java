package com.uber.viajes.domain.model.gateway;

import com.uber.viajes.domain.model.UsuarioResumen;

import java.util.Optional;

public interface UsuarioGateway {

    /**
     * Datos del usuario en ms-usuarios, o vacío si no existe.
     */
    Optional<UsuarioResumen> buscarUsuario(Long usuarioId);

    /**
     * Devuelve el chat ID de Telegram del usuario, o vacio si no lo tiene registrado
     * o ms-usuarios no esta disponible.
     */
    Optional<String> obtenerChatIdTelegram(Long usuarioId);
}

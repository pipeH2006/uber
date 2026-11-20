package com.uber.notificaciones.domain.model.gateway;

public interface UsuarioGateway {

    /**
     * Guarda el chat de Telegram en el usuario de ms-usuarios.
     *
     * @return {@code false} si el usuario no existe
     */
    boolean vincularTelegram(Long usuarioId, String chatId);
}

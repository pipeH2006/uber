package com.uber.notificaciones.domain.model.gateway;

public interface UsuarioGateway {
    boolean vincularTelegram(Long usuarioId, String chatId);

    int vincularPorTelefono(String telefono, String chatId);
}

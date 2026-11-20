package com.uber.notificaciones.domain.model.gateway;

public interface UsuarioGateway {

    /**
     * Guarda el chat de Telegram en el usuario de ms-usuarios.
     *
     * @return {@code false} si el usuario no existe
     */
    boolean vincularTelegram(Long usuarioId, String chatId);

    /**
     * Vincula el chat a todos los usuarios cuyo teléfono coincide con el dado.
     *
     * @return cuántos usuarios se vincularon (0 si ninguno usa ese teléfono)
     */
    int vincularPorTelefono(String telefono, String chatId);
}

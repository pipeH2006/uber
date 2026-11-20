package com.uber.notificaciones.domain.model.gateway;

import com.uber.notificaciones.domain.model.ActualizacionTelegram;

import java.util.List;

/**
 * Puerto de salida (gateway) hacia el proveedor de mensajería externo
 * (Telegram).
 */
public interface TelegramGateway {

    void enviar(String chatId, String texto);

    /**
     * Mensajes que los usuarios le han escrito al bot desde {@code offset}. Devuelve una lista
     * vacía si no hay novedades o si Telegram no está disponible.
     */
    List<ActualizacionTelegram> obtenerActualizaciones(long offset);
}

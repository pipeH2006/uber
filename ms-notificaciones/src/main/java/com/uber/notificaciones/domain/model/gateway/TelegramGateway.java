package com.uber.notificaciones.domain.model.gateway;

/**
 * Puerto de salida (gateway) hacia el proveedor de mensajería externo
 * (Telegram).
 */
public interface TelegramGateway {

    void enviar(String chatId, String texto);
}

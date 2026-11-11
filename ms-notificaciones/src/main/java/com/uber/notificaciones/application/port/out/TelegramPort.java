package com.uber.notificaciones.application.port.out;

/**
 * Puerto de salida (driven port) hacia el proveedor de mensajería externo
 * (Telegram).
 */
public interface TelegramPort {

    void enviar(String chatId, String texto);
}

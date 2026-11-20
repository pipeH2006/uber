package com.uber.notificaciones.domain.model;

/**
 * Mensaje recibido por el bot de Telegram. {@code chatId} y {@code texto} pueden ser nulos
 * cuando la actualización no es un mensaje de texto.
 */
public record ActualizacionTelegram(long updateId, String chatId, String texto) {
}

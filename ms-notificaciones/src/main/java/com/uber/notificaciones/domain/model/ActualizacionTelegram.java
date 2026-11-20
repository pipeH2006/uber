package com.uber.notificaciones.domain.model;

/**
 * Mensaje recibido por el bot de Telegram. {@code chatId}, {@code texto} y {@code telefono}
 * pueden ser nulos según el tipo de mensaje. {@code telefono} llega cuando el usuario comparte un
 * contacto; {@code contactoPropio} indica que ese contacto es su propio número (y no el de otra persona).
 */
public record ActualizacionTelegram(long updateId, String chatId, String texto, String telefono, boolean contactoPropio) {

    public ActualizacionTelegram(long updateId, String chatId, String texto) {
        this(updateId, chatId, texto, null, false);
    }
}

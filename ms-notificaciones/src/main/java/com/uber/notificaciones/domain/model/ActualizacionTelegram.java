package com.uber.notificaciones.domain.model;

public record ActualizacionTelegram(long updateId, String chatId, String texto, String telefono, boolean contactoPropio) {
    public ActualizacionTelegram(long updateId, String chatId, String texto) {
        this(updateId, chatId, texto, null, false);
    }
}

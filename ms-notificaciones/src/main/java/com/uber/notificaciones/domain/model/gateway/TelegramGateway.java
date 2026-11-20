package com.uber.notificaciones.domain.model.gateway;

import com.uber.notificaciones.domain.model.ActualizacionTelegram;

import java.util.List;

public interface TelegramGateway {
    void enviar(String chatId, String texto);

    void solicitarContacto(String chatId, String texto);

    List<ActualizacionTelegram> obtenerActualizaciones(long offset);
}

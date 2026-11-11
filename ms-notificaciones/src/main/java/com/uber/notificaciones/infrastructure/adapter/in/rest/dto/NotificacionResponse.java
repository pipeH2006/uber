package com.uber.notificaciones.infrastructure.adapter.in.rest.dto;

import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        String destinatario,
        Canal canal,
        TipoNotificacion tipo,
        String mensaje,
        EstadoNotificacion estado,
        LocalDateTime fechaEnvio,
        int intentos
) {

    public static NotificacionResponse from(Notificacion n) {
        return new NotificacionResponse(
                n.getId(),
                n.getDestinatario(),
                n.getCanal(),
                n.getTipo(),
                n.getMensaje(),
                n.getEstado(),
                n.getFechaEnvio(),
                n.getIntentos()
        );
    }
}

package com.uber.notificaciones.infrastructure.adapter.in.rest.dto;

import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;

public record NotificacionRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        Canal canal,

        TipoNotificacion tipo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        EstadoNotificacion estado
) {
}

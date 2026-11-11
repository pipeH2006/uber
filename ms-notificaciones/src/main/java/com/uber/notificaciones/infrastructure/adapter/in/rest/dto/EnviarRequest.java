package com.uber.notificaciones.infrastructure.adapter.in.rest.dto;

import com.uber.notificaciones.domain.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;

public record EnviarRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        TipoNotificacion tipo
) {
}

package com.uber.notificaciones.infrastructure.adapter.in.rest.dto;

import com.uber.notificaciones.domain.model.TipoNotificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EnvioMasivoRequest(
        @NotEmpty(message = "La lista de destinatarios es obligatoria")
        List<@NotBlank(message = "El destinatario no puede estar vacío") String> destinatarios,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        TipoNotificacion tipo
) {
}

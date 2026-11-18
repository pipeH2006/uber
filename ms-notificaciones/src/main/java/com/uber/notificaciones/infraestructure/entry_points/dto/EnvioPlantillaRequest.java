package com.uber.notificaciones.infraestructure.entry_points.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record EnvioPlantillaRequest(
        @NotBlank(message = "El código de plantilla es obligatorio")
        String codigo,

        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        Map<String, String> variables
) {
}

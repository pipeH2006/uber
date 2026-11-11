package com.uber.notificaciones.infrastructure.adapter.in.rest.dto;

import com.uber.notificaciones.domain.model.Plantilla;

public record PlantillaResponse(
        Long id,
        String codigo,
        String asunto,
        String mensaje
) {

    public static PlantillaResponse from(Plantilla p) {
        return new PlantillaResponse(p.getId(), p.getCodigo(), p.getAsunto(), p.getMensaje());
    }
}

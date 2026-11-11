package com.uber.usuarios.infrastructure.adapter.in.rest.dto;

public record PromedioResponse(
        Long usuarioId,
        double promedio,
        int totalCalificaciones
) {
}

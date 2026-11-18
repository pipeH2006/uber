package com.uber.usuarios.infraestructure.entry_points.dto;

public record PromedioResponse(
        Long usuarioId,
        double promedio,
        int totalCalificaciones
) {
}

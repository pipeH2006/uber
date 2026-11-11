package com.uber.usuarios.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalificacionRequest(
        @NotNull(message = "El id del calificador es obligatorio")
        Long calificadorId,

        @NotNull(message = "El puntaje es obligatorio")
        @Min(value = 1, message = "El puntaje mínimo es 1")
        @Max(value = 5, message = "El puntaje máximo es 5")
        Integer puntaje,

        @Size(max = 500)
        String comentario
) {
}

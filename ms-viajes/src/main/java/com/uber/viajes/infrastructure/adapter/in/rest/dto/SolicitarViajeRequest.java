package com.uber.viajes.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record SolicitarViajeRequest(
        @NotNull(message = "El id del pasajero es obligatorio")
        Long pasajeroId,

        @NotNull(message = "El id del conductor es obligatorio")
        Long conductorId,

        double origenLat,
        double origenLng,
        String origenDireccion,
        double destinoLat,
        double destinoLng,
        String destinoDireccion
) {
}

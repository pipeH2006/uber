package com.uber.viajes.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record EstimarViajeRequest(
        @NotNull(message = "La latitud de origen es obligatoria")
        Double origenLat,

        @NotNull(message = "La longitud de origen es obligatoria")
        Double origenLng,

        String origenDireccion,

        @NotNull(message = "La latitud de destino es obligatoria")
        Double destinoLat,

        @NotNull(message = "La longitud de destino es obligatoria")
        Double destinoLng,

        String destinoDireccion
) {
}

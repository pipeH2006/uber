package com.uber.viajes.infrastructure.adapter.in.rest.dto;

import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViajeResponse(
        Long id,
        Long pasajeroId,
        Long conductorId,
        double origenLat,
        double origenLng,
        String origenDireccion,
        double destinoLat,
        double destinoLng,
        String destinoDireccion,
        double distanciaKm,
        double duracionMin,
        BigDecimal costoEstimado,
        BigDecimal costoFinal,
        String moneda,
        EstadoViaje estado,
        LocalDateTime fechaSolicitud,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {

    public static ViajeResponse from(Viaje v) {
        return new ViajeResponse(
                v.getId(),
                v.getPasajeroId(),
                v.getConductorId(),
                v.getOrigenLat(),
                v.getOrigenLng(),
                v.getOrigenDireccion(),
                v.getDestinoLat(),
                v.getDestinoLng(),
                v.getDestinoDireccion(),
                v.getDistanciaKm(),
                v.getDuracionMin(),
                v.getCostoEstimado(),
                v.getCostoFinal(),
                v.getMoneda(),
                v.getEstado(),
                v.getFechaSolicitud(),
                v.getFechaInicio(),
                v.getFechaFin()
        );
    }
}

package com.uber.viajes.infraestructure.mapper;

import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.infraestructure.driver_adapters.jpa_repository.ViajeData;
import org.springframework.stereotype.Component;

@Component
public class MapperViaje {

    public Viaje toViaje(ViajeData data) {
        return Viaje.builder()
                .id(data.getId())
                .pasajeroId(data.getPasajeroId())
                .conductorId(data.getConductorId())
                .origenLat(data.getOrigenLat())
                .origenLng(data.getOrigenLng())
                .origenDireccion(data.getOrigenDireccion())
                .destinoLat(data.getDestinoLat())
                .destinoLng(data.getDestinoLng())
                .destinoDireccion(data.getDestinoDireccion())
                .distanciaKm(data.getDistanciaKm())
                .duracionMin(data.getDuracionMin())
                .costoEstimado(data.getCostoEstimado())
                .costoFinal(data.getCostoFinal())
                .moneda(data.getMoneda())
                .estado(data.getEstado())
                .fechaSolicitud(data.getFechaSolicitud())
                .fechaInicio(data.getFechaInicio())
                .fechaFin(data.getFechaFin())
                .build();
    }

    public ViajeData toData(Viaje viaje) {
        return ViajeData.builder()
                .id(viaje.getId())
                .pasajeroId(viaje.getPasajeroId())
                .conductorId(viaje.getConductorId())
                .origenLat(viaje.getOrigenLat())
                .origenLng(viaje.getOrigenLng())
                .origenDireccion(viaje.getOrigenDireccion())
                .destinoLat(viaje.getDestinoLat())
                .destinoLng(viaje.getDestinoLng())
                .destinoDireccion(viaje.getDestinoDireccion())
                .distanciaKm(viaje.getDistanciaKm())
                .duracionMin(viaje.getDuracionMin())
                .costoEstimado(viaje.getCostoEstimado())
                .costoFinal(viaje.getCostoFinal())
                .moneda(viaje.getMoneda())
                .estado(viaje.getEstado())
                .fechaSolicitud(viaje.getFechaSolicitud())
                .fechaInicio(viaje.getFechaInicio())
                .fechaFin(viaje.getFechaFin())
                .build();
    }
}

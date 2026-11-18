package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import com.uber.viajes.domain.model.Seguimiento;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.MapsGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeguimientoViajeUseCase {

    private final ViajeGateway viajeGateway;
    private final MapsGateway mapsGateway;

    public Seguimiento seguir(Long id) {
        Viaje viaje = viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        Ruta ruta = mapsGateway.calcularRuta(
                new Coordenada(viaje.getOrigenLat(), viaje.getOrigenLng()),
                new Coordenada(viaje.getDestinoLat(), viaje.getDestinoLng()));

        return new Seguimiento(
                viaje.getId(),
                viaje.getEstado(),
                ruta.distanciaKm(),
                ruta.duracionMin(),
                viaje.getCostoEstimado());
    }
}

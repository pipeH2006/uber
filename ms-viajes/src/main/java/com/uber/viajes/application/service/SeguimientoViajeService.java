package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.Seguimiento;
import com.uber.viajes.application.port.in.SeguimientoViajeUseCase;
import com.uber.viajes.application.port.out.MapsClientPort;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import com.uber.viajes.domain.model.Viaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeguimientoViajeService implements SeguimientoViajeUseCase {

    private final ViajeRepositoryPort repository;
    private final MapsClientPort mapsClient;

    @Override
    public Seguimiento seguir(Long id) {
        Viaje viaje = repository.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        Ruta ruta = mapsClient.calcularRuta(
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

package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Caso de uso CRUD del agregado Viaje.
 */
@RequiredArgsConstructor
public class ViajeCrudUseCase {

    private final ViajeGateway viajeGateway;

    public Viaje crear(Viaje viaje) {
        viaje.setEstado(EstadoViaje.SOLICITADO);
        viaje.setFechaSolicitud(LocalDateTime.now());
        if (viaje.getMoneda() == null || viaje.getMoneda().isBlank()) {
            viaje.setMoneda("USD");
        }
        return viajeGateway.guardar(viaje);
    }

    public Viaje buscarPorId(Long id) {
        return viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));
    }

    public List<Viaje> listar() {
        return viajeGateway.listar();
    }

    public Viaje actualizar(Long id, Viaje datos) {
        Viaje existente = buscarPorId(id);
        if (datos.getPasajeroId() != null) {
            existente.setPasajeroId(datos.getPasajeroId());
        }
        if (datos.getConductorId() != null) {
            existente.setConductorId(datos.getConductorId());
        }
        if (datos.getOrigenDireccion() != null) {
            existente.setOrigenDireccion(datos.getOrigenDireccion());
        }
        if (datos.getDestinoDireccion() != null) {
            existente.setDestinoDireccion(datos.getDestinoDireccion());
        }
        if (datos.getCostoEstimado() != null) {
            existente.setCostoEstimado(datos.getCostoEstimado());
        }
        if (datos.getCostoFinal() != null) {
            existente.setCostoFinal(datos.getCostoFinal());
        }
        if (datos.getMoneda() != null) {
            existente.setMoneda(datos.getMoneda());
        }
        return viajeGateway.guardar(existente);
    }

    public void eliminar(Long id) {
        if (viajeGateway.buscarPorId(id).isEmpty()) {
            throw new ViajeNoEncontradoException(id);
        }
        viajeGateway.eliminar(id);
    }
}

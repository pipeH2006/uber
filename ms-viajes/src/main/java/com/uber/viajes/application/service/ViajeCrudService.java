package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.ViajeCrudUseCase;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViajeCrudService implements ViajeCrudUseCase {

    private final ViajeRepositoryPort repository;

    @Override
    public Viaje crear(Viaje viaje) {
        viaje.setEstado(EstadoViaje.SOLICITADO);
        viaje.setFechaSolicitud(LocalDateTime.now());
        if (viaje.getMoneda() == null || viaje.getMoneda().isBlank()) {
            viaje.setMoneda("USD");
        }
        return repository.guardar(viaje);
    }

    @Override
    public Viaje buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));
    }

    @Override
    public List<Viaje> listar() {
        return repository.listar();
    }

    @Override
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
        return repository.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new ViajeNoEncontradoException(id);
        }
        repository.eliminar(id);
    }
}

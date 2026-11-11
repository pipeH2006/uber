package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.CancelarViajeUseCase;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelarViajeService implements CancelarViajeUseCase {

    private final ViajeRepositoryPort repository;

    @Override
    public Viaje cancelar(Long id) {
        Viaje viaje = repository.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() == EstadoViaje.FINALIZADO || viaje.getEstado() == EstadoViaje.CANCELADO) {
            throw new EstadoViajeInvalidoException("No se puede cancelar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.CANCELADO);
        return repository.guardar(viaje);
    }
}

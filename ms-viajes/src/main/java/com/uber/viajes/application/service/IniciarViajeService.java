package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.IniciarViajeUseCase;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IniciarViajeService implements IniciarViajeUseCase {

    private final ViajeRepositoryPort repository;

    @Override
    public Viaje iniciar(Long id) {
        Viaje viaje = repository.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() != EstadoViaje.ASIGNADO) {
            throw new EstadoViajeInvalidoException("No se puede iniciar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.EN_CURSO);
        viaje.setFechaInicio(LocalDateTime.now());
        return repository.guardar(viaje);
    }
}

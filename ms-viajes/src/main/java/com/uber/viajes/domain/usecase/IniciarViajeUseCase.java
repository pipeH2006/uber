package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class IniciarViajeUseCase {

    private final ViajeGateway viajeGateway;

    public Viaje iniciar(Long id) {
        Viaje viaje = viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() != EstadoViaje.ASIGNADO) {
            throw new EstadoViajeInvalidoException("No se puede iniciar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.EN_CURSO);
        viaje.setFechaInicio(LocalDateTime.now());
        return viajeGateway.guardar(viaje);
    }
}

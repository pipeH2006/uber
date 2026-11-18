package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelarViajeUseCase {

    private final ViajeGateway viajeGateway;

    public Viaje cancelar(Long id) {
        Viaje viaje = viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() == EstadoViaje.FINALIZADO || viaje.getEstado() == EstadoViaje.CANCELADO) {
            throw new EstadoViajeInvalidoException("No se puede cancelar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.CANCELADO);
        return viajeGateway.guardar(viaje);
    }
}

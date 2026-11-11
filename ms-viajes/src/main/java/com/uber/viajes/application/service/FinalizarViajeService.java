package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.FinalizarViajeUseCase;
import com.uber.viajes.application.port.out.NotificacionPort;
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
public class FinalizarViajeService implements FinalizarViajeUseCase {

    private final ViajeRepositoryPort repository;
    private final NotificacionPort notificacion;

    @Override
    public Viaje finalizar(Long id) {
        Viaje viaje = repository.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() != EstadoViaje.EN_CURSO) {
            throw new EstadoViajeInvalidoException("No se puede finalizar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.FINALIZADO);
        viaje.setCostoFinal(viaje.getCostoEstimado());
        viaje.setFechaFin(LocalDateTime.now());
        Viaje guardado = repository.guardar(viaje);
        notificacion.notificar("pasajero-" + viaje.getPasajeroId(), "Tu viaje ha finalizado");
        return guardado;
    }
}

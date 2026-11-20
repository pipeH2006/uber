package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.UsuarioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class FinalizarViajeUseCase {
    private final ViajeGateway viajeGateway;
    private final NotificacionGateway notificacionGateway;
    private final UsuarioGateway usuarioGateway;

    public Viaje finalizar(Long id) {
        Viaje viaje = viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() != EstadoViaje.EN_CURSO) {
            throw new EstadoViajeInvalidoException("No se puede finalizar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.FINALIZADO);
        viaje.setCostoFinal(viaje.getCostoEstimado());
        viaje.setFechaFin(LocalDateTime.now());
        Viaje guardado = viajeGateway.guardar(viaje);
        avisar(viaje.getPasajeroId(), "Tu viaje ha finalizado");
        avisar(viaje.getConductorId(),
                "Finalizaste el viaje. Ganancia estimada: " + viaje.getCostoFinal() + " " + viaje.getMoneda());
        return guardado;
    }

    private void avisar(Long usuarioId, String mensaje) {
        if (usuarioId != null) {
            usuarioGateway.obtenerChatIdTelegram(usuarioId)
                    .ifPresent(chatId -> notificacionGateway.notificar(chatId, mensaje));
        }
    }
}

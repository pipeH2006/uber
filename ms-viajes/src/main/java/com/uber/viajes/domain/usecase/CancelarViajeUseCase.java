package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.UsuarioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CancelarViajeUseCase {

    private final ViajeGateway viajeGateway;
    private final NotificacionGateway notificacionGateway;
    private final UsuarioGateway usuarioGateway;

    public Viaje cancelar(Long id) {
        Viaje viaje = viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        // Solo se puede cancelar antes de que el conductor inicie el viaje.
        if (viaje.getEstado() != EstadoViaje.SOLICITADO && viaje.getEstado() != EstadoViaje.ASIGNADO) {
            throw new EstadoViajeInvalidoException("No se puede cancelar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.CANCELADO);
        Viaje guardado = viajeGateway.guardar(viaje);
        avisar(viaje.getPasajeroId(), "Tu viaje fue cancelado");
        avisar(viaje.getConductorId(), "El viaje que tenías asignado fue cancelado");
        return guardado;
    }

    private void avisar(Long usuarioId, String mensaje) {
        if (usuarioId != null) {
            usuarioGateway.obtenerChatIdTelegram(usuarioId)
                    .ifPresent(chatId -> notificacionGateway.notificar(chatId, mensaje));
        }
    }
}

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
public class IniciarViajeUseCase {

    private final ViajeGateway viajeGateway;
    private final NotificacionGateway notificacionGateway;
    private final UsuarioGateway usuarioGateway;

    public Viaje iniciar(Long id) {
        Viaje viaje = viajeGateway.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));

        if (viaje.getEstado() != EstadoViaje.ASIGNADO) {
            throw new EstadoViajeInvalidoException("No se puede iniciar un viaje en estado " + viaje.getEstado());
        }
        viaje.setEstado(EstadoViaje.EN_CURSO);
        viaje.setFechaInicio(LocalDateTime.now());
        Viaje guardado = viajeGateway.guardar(viaje);
        avisar(viaje.getPasajeroId(), "Tu viaje ha comenzado, ¡buen viaje!");
        avisar(viaje.getConductorId(), "Comenzaste el viaje. ¡Buen camino!");
        return guardado;
    }

    private void avisar(Long usuarioId, String mensaje) {
        if (usuarioId != null) {
            usuarioGateway.obtenerChatIdTelegram(usuarioId)
                    .ifPresent(chatId -> notificacionGateway.notificar(chatId, mensaje));
        }
    }
}

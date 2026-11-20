package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.UsuarioNoEncontradoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Estimacion;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.UsuarioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class SolicitarViajeUseCase {

    private final EstimarViajeUseCase estimarViajeUseCase;
    private final NotificacionGateway notificacionGateway;
    private final ViajeGateway viajeGateway;
    private final UsuarioGateway usuarioGateway;

    public Viaje solicitar(Long pasajeroId, Long conductorId, Coordenada origen, Coordenada destino) {
        validarUsuarioExiste(pasajeroId);
        validarUsuarioExiste(conductorId);

        // La estimación (ruta + tarifa USD/COP) se obtiene a través del caso de uso de estimación,
        // que a su vez invoca RutasGateway.calcularRuta y TipoCambioGateway.obtenerTipoCambio("COP").
        Estimacion estimacion = estimarViajeUseCase.estimar(origen, destino);

        Viaje viaje = Viaje.builder()
                .pasajeroId(pasajeroId)
                .conductorId(conductorId)
                .origenLat(origen.lat())
                .origenLng(origen.lng())
                .destinoLat(destino.lat())
                .destinoLng(destino.lng())
                .distanciaKm(estimacion.distanciaKm())
                .duracionMin(estimacion.duracionMin())
                .costoEstimado(estimacion.costoUSD())
                .moneda("USD")
                .estado(EstadoViaje.ASIGNADO)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        Viaje guardado = viajeGateway.guardar(viaje);
        usuarioGateway.obtenerChatIdTelegram(conductorId)
                .ifPresent(chatId -> notificacionGateway.notificar(chatId, "Se te ha asignado un nuevo viaje"));
        usuarioGateway.obtenerChatIdTelegram(pasajeroId)
                .ifPresent(chatId -> notificacionGateway.notificar(chatId,
                        "Tu viaje #" + guardado.getId() + " fue asignado a un conductor y va en camino"));
        return guardado;
    }

    private void validarUsuarioExiste(Long usuarioId) {
        if (!usuarioGateway.usuarioExiste(usuarioId)) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
    }
}

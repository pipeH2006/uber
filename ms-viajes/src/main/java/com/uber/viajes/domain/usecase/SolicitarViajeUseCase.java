package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.UsuarioNoEncontradoException;
import com.uber.viajes.domain.exception.UsuarioNoHabilitadoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Estimacion;
import com.uber.viajes.domain.model.UsuarioResumen;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.UsuarioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Locale;

@RequiredArgsConstructor
public class SolicitarViajeUseCase {

    private final EstimarViajeUseCase estimarViajeUseCase;
    private final NotificacionGateway notificacionGateway;
    private final ViajeGateway viajeGateway;
    private final UsuarioGateway usuarioGateway;

    public Viaje solicitar(Long pasajeroId, Long conductorId, Coordenada origen, Coordenada destino) {
        validarPasajero(pasajeroId);
        validarConductor(conductorId);

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
                .ifPresent(chatId -> notificacionGateway.notificar(chatId,
                        "Se te ha asignado un nuevo viaje: "
                                + String.format(Locale.US, "%.1f", estimacion.distanciaKm()) + " km · "
                                + Math.round(estimacion.duracionMin()) + " min · "
                                + estimacion.costoUSD() + " USD"));
        usuarioGateway.obtenerChatIdTelegram(pasajeroId)
                .ifPresent(chatId -> notificacionGateway.notificar(chatId,
                        "Tu viaje #" + guardado.getId() + " fue asignado a un conductor y va en camino"));
        return guardado;
    }

    private void validarPasajero(Long pasajeroId) {
        UsuarioResumen pasajero = buscarUsuario(pasajeroId);
        if (!pasajero.estaActivo()) {
            throw new UsuarioNoHabilitadoException(
                    "El pasajero " + pasajeroId + " no está activo (estado " + pasajero.estado() + ")");
        }
    }

    private void validarConductor(Long conductorId) {
        UsuarioResumen conductor = buscarUsuario(conductorId);
        if (!conductor.estaActivo()) {
            throw new UsuarioNoHabilitadoException(
                    "El conductor " + conductorId + " no está activo (estado " + conductor.estado() + ")");
        }
        if (!conductor.documentoVerificado()) {
            throw new UsuarioNoHabilitadoException(
                    "El conductor " + conductorId + " no tiene el documento verificado (estado " + conductor.estadoDocumento() + ")");
        }
    }

    private UsuarioResumen buscarUsuario(Long usuarioId) {
        return usuarioGateway.buscarUsuario(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
    }
}
